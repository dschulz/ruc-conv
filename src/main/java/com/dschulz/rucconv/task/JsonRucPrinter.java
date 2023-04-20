package com.dschulz.rucconv.task;

import com.dschulz.rucconv.model.Contribuyente;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

public class JsonRucPrinter extends PrintWriter implements RecordListExporter<Contribuyente> {

    public JsonRucPrinter(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    @Override
    public void export(List<Contribuyente> lista) {
        this.println("{\n  \"rucs\": [");

        Iterator<Contribuyente> iterator = lista.iterator();

        while (iterator.hasNext()) {
            Contribuyente c = iterator.next();

            boolean agregarComaAlFinal = iterator.hasNext();
            this.println(formatearJsonObject(c, agregarComaAlFinal));
        }

        this.println("  ]\n}\n");
    }

    private String formatearJsonObject(Contribuyente c, boolean agregarComaAlFinal) {
        return "    { \"doc\": " + c.getRuc()
            + ", \"dv\": " + c.getVerificador()
            + ", \"denominacionOriginal\": " + doubleQuote(c.getDenominacion())
            + ", \"denominacionCorregida\": " + (doubleQuote(c.getDenominacionCorregida() != null ? c.getDenominacionCorregida() : c.getDenominacion()))
            + ", \"estado\": " + String.format("\"%s\"", c.getEstado())
            + ", \"ruc\": " + String.format("\"%s-%s\"", c.getRuc(), c.getVerificador() )
            + (agregarComaAlFinal  ? " }," : " }" ) ;
    }

    private String doubleQuote(String orig){
        return "\"" +
            orig.replaceAll("\"", "\\\\\"" ) +
            "\"";
    }
}
