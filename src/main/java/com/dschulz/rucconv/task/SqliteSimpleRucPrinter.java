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

public class SqliteSimpleRucPrinter extends PrintWriter implements RecordListExporter<Contribuyente> {
    public SqliteSimpleRucPrinter(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public SqliteSimpleRucPrinter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public void export(List<Contribuyente> lista) {

        String INSTANTE = ZonedDateTime.now().format( DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.forLanguageTag("es")));

        String ddl = """
            CREATE TABLE IF NOT EXISTS ruc (
               id INTEGER PRIMARY KEY AUTOINCREMENT,
               doc TEXT UNIQUE NOT NULL,
               dv INTEGER NOT NULL,
               denominacion TEXT NOT NULL,
               estado TEXT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS estado_ruc (
              id INTEGER,
              estado TEXT
            );

            """;

        String insEstados = """
            INSERT INTO estado_ruc(id,estado) VALUES
             (1, 'ACTIVO'),
             (2, 'CANCELADO'),
             (3, 'SUSPENSIÓN TEMPORAL'),
             (4, 'BLOQUEADO'),
             (5, 'CANCELADO DEFINITIVO') ;
            """;



        this.println("-- Volcado RUCs simplificado para SQLite3");
        this.println("-- Recuperado de la basura que publica la SET");
        this.println(String.format("-- %s ",  INSTANTE));
        this.println();

        this.println(".echo off");
        this.println(".changes off");
        this.println(".timer off");
        this.println(".eqp off");
        this.println();



        surroundWithTransaction( () -> {
            this.println(ddl);
            this.println(insEstados);
            String plantilla = "INSERT INTO ruc (doc, dv, denominacion, estado) VALUES ('%s', %d, %s, %d );";

            for (Contribuyente c : lista){

                String sql = String.format(plantilla,
                    c.getRuc(),
                    c.getVerificador(),
                    (c.getDenominacionCorregida()!=null ) ? singleQuote( c.getDenominacionCorregida() ) : singleQuote( c.getDenominacion() ) ,
                    estadoNumerico(c.getEstado())
                );
                this.println(sql);
            }




            this.println();

        });

        this.println(".echo on");
        this.println(".changes on");
        this.println(".timer on");
        this.println(".eqp on");
    }

    private NumberFormat numberFormat(){
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator('.');

        return new DecimalFormat ("###,###", symbols);
    }

    private String singleQuote(String orig){
        return "'" +
            orig.replaceAll("'", "''") + "'";
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
