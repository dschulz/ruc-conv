package com.dschulz.rucconv.task;

import com.dschulz.rucconv.model.Contribuyente;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

public class OracleDialectRucPrinter extends PrintWriter implements RecordListExporter<Contribuyente> {
    public OracleDialectRucPrinter(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public OracleDialectRucPrinter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public void export(List<Contribuyente> lista) {


        String ddl = """
            CREATE TABLE ruc (
               id NUMBER PRIMARY KEY,
               doc VARCHAR2(255) UNIQUE NOT NULL,
               dv NUMBER NOT NULL,
               denominacionOriginal VARCHAR2(255) NOT NULL,
               denominacionCorregida VARCHAR2(255),
               estado VARCHAR(50)
            );

            CREATE SEQUENCE ruc_id_seq START WITH 1 INCREMENT BY 1;
            """;


        this.println("-- Volcado RUCs para Oracle");
        this.println("-- Recuperado de la basura que publica la DNIT");
        this.println(String.format("-- %s ", LocalDateTime.now()));
        this.println();


        surroundWithTransaction(() -> {

            this.println(ddl);

            String plantilla = "INSERT INTO ruc (id, doc, dv, denominacionOriginal, denominacionCorregida) VALUES(ruc_id_seq.nextval, %s, %d, %s, %s, %s);";


            for (Contribuyente c : lista) {

                String sql = String.format(plantilla,
                    singleQuote(c.getRuc()),
                    c.getVerificador(),
                    singleQuote(c.getDenominacion().replaceAll("\"", "")),
                    (c.getDenominacionCorregida() != null) ? singleQuote( c.getDenominacionCorregida() ) : "null",
                    singleQuote(c.getEstado())
                );
                this.println(sql);

            }

            this.println("UPDATE ruc SET denominacionCorregida = denominacionOriginal WHERE denominacionCorregida IS NULL;");
            this.println("-- Opcional: ");
            this.println("-- ALTER TABLE ruc DROP COLUMN denominacionOriginal ;");
            this.println("-- ALTER TABLE ruc RENAME COLUMN denominacionCorregida TO denominacion;");
            this.println();
        });

    }

    private String singleQuote(String orig){
        String sb = "'" +
            orig.replaceAll("'", "\\\\'") +
            "'";
        return sb;
    }


    private void surroundWithTransaction(SQLWriter r) {
        this.println("\nBEGIN");
        r.writeSQL();
        this.println("COMMIT;\nEND;");
    }

}
