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

public class SqliteRucPrinter extends PrintWriter implements RecordListExporter<Contribuyente> {
    public SqliteRucPrinter(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public SqliteRucPrinter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public void export(List<Contribuyente> lista) {

        String INSTANTE = ZonedDateTime.now().format( DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.forLanguageTag("es")));

        String ddl = """
CREATE TABLE IF NOT EXISTS ruc (
   id INTEGER PRIMARY KEY AUTOINCREMENT,
   doc TEXT UNIQUE NOT NULL,
   dv INTEGER NOT NULL,
   denominacionOriginal TEXT NOT NULL,
   denominacionCorregida TEXT,
   estado TEXT NOT NULL
);
""";

        String metaDdl = """
CREATE TABLE IF NOT EXISTS meta (
   k TEXT,
   v TEXT
);
           """;

        String ftsDdl = """
-- Opcional: Indice full-text para búsquedas eficientes - activo por defecto
CREATE VIRTUAL TABLE ruc_fts USING FTS5 (
   doc,
   denominacion,
   tokenize = 'unicode61'
);
""";

        String ftsLoad = """
-- Opcional - activo por defecto
INSERT INTO ruc_fts (doc, denominacion) SELECT doc, denominacion FROM ruc;
""";

        String metaLoad = String.format("""
            INSERT INTO meta (k,v) VALUES ('NumRegistros', '%s' );
            INSERT INTO meta (k,v) VALUES ('Actualizado', '%s');
            """, numberFormat().format(lista.size()), INSTANTE);


        this.println("-- Volcado RUCs para SQLite3");
        this.println("-- Recuperado de la basura que publica la DNIT");
        this.println(String.format("-- %s ",  INSTANTE));
        this.println();
        this.println(".echo off");
        this.println(".changes off");
        this.println(".timer off");
        this.println(".eqp off");
        this.println();
        this.println(metaDdl);
        this.println(metaLoad);


        surroundWithTransaction( () -> {
            this.println(ddl);
            this.println(ftsDdl);

            String plantilla = "INSERT INTO ruc (doc, dv, denominacionOriginal, denominacionCorregida, estado) VALUES (%s, %d, %s, %s, '%s');";

            for (Contribuyente c : lista){

                String sql = String.format(plantilla,
                    singleQuote(c.getRuc()),
                    c.getVerificador(),
                    singleQuote(c.getDenominacion()),
                    (c.getDenominacionCorregida()!=null ) ? singleQuote( c.getDenominacionCorregida() ) : "null" ,
                    c.getEstado()
                );
                this.println(sql);
            }


            this.println("UPDATE ruc SET denominacionCorregida = denominacionOriginal WHERE denominacionCorregida IS NULL;");
            this.println("-- Opcional: comentar las siguientes lineas para evitar que se elimine la columna original ");
            this.println("ALTER TABLE ruc DROP COLUMN denominacionOriginal ;");
            this.println("ALTER TABLE ruc RENAME COLUMN denominacionCorregida TO denominacion;");
            this.println(ftsLoad);
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

}
