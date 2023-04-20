package com.dschulz.rucconv.task;

import com.dschulz.rucconv.model.Contribuyente;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

public class CsvRucPrinter extends PrintWriter implements RecordListExporter<Contribuyente> {

    public CsvRucPrinter(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public CsvRucPrinter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    @Override
    public void export(List<Contribuyente> lista) {
        this.println("ruc;dv;denominacion_original;denominacion_corregida;estado;ruc_anterior");

        for (Contribuyente c : lista) {
            this.println(formatearLineaCsv(c));
        }
    }

    private String formatearLineaCsv(Contribuyente c) {
        return String.format("%s|%d|%s|%s|%s",
            doubleQuote(c.getRuc()),
            c.getVerificador(),
            doubleQuote((c.getDenominacionCorregida()!=null ? c.getDenominacionCorregida() : c.getDenominacion())),
            doubleQuote(c.getEstado()),
            doubleQuote(c.getRucAnterior())
        );
    }

    // Segun RFC 4180 los " deben sustituirse por ""
    // aunque en el campo denominacionCorregida no deberia haber el caracter "
    private String doubleQuote(String orig){
        return "\"" +
            orig.replaceAll("\"", "\"\"" ) +
            "\"";
    }
}
