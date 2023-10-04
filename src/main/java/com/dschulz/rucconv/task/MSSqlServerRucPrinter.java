package com.dschulz.rucconv.task;

import com.dschulz.rucconv.model.Contribuyente;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class MSSqlServerRucPrinter extends PrintWriter implements RecordListExporter<Contribuyente> {
    public MSSqlServerRucPrinter(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public MSSqlServerRucPrinter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public void export(List<Contribuyente> lista) {

        String INSTANTE = ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.forLanguageTag("es")));

        String ddl = """
            IF NOT EXISTS (SELECT TOP 1 * FROM sysobjects WHERE name='RUC' and xtype='U')
            	CREATE TABLE dbo.RUC(
            		id int NOT NULL IDENTITY (1, 1),
            		doc nvarchar(255) NULL,
            		dv tinyint NULL,
            		denominacion nvarchar(255) NULL,
            		ESTADO tinyint NULL
            		)  ON [PRIMARY];
            	GO
            GO

            /*
            Estados:

            ACTIVO = 1
            CANCELADO = 2
            SUSPENSION TEMPORAL = 3
            BLOQUEADO = 4
            CANCELADO DEFINITIVO = 5
            */

            """;


        this.println("-- Volcado RUCs para MS SqlServer");
        this.println("-- Recuperado de la basura que publica la SET");
        this.println(String.format("-- %s ", INSTANTE));
        this.println();
        this.println();


        this.println(ddl);
        this.println("");

        String plantilla = "INSERT INTO ruc (doc, dv, denominacion, estado) VALUES ('%s', %d, '%s', %d);";


        surroundWithTransaction(() -> {


            int tam = lista.size();
            for (int i = 0; i < tam; i++) {
                var c = lista.get(i);

                if ((i>0) && (i % 50000 == 0)) {
                    this.println("COMMIT;\n");
                    this.println("\nBEGIN TRANSACTION;");
                }

                String sql = String.format(plantilla,
                    c.getRuc(),
                    c.getVerificador(),
                    (c.getDenominacionCorregida() != null) ? singleQuote(c.getDenominacionCorregida()) : singleQuote(c.getDenominacion()),
                    estadoNumerico(c.getEstado())
                );
                this.println(sql);
            }
        });

        this.println();
        this.println();


        this.println();
        this.println("/*  FIN */");

    }

    private NumberFormat numberFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator('.');

        return new DecimalFormat("###,###", symbols);
    }

    private String singleQuote(String orig) {
        return
            orig.replaceAll("'", "''");
    }

    private void surroundWithTransaction(SQLWriter r) {
        this.println("\nBEGIN TRANSACTION;");
        r.writeSQL();
        this.println("COMMIT;\n");
    }

    private int estadoNumerico(String estado) {
        if (estado.equalsIgnoreCase("ACTIVO"))
            return 1;
        if (estado.equalsIgnoreCase("CANCELADO"))
            return 2;
        if (estado.equalsIgnoreCase("SUSPENSION TEMPORAL"))
            return 3;
        if (estado.equalsIgnoreCase("BLOQUEADO"))
            return 4;
        if (estado.equalsIgnoreCase("CANCELADO DEFINITIVO"))
            return 5;

        return 99; // desconocido
    }

}
