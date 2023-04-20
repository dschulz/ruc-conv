package com.dschulz.rucconv.task;

import com.dschulz.rucconv.model.Contribuyente;
import com.dschulz.rucconv.util.DigitoVerificador;
import javafx.concurrent.Task;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.dschulz.rucconv.task.ObtenerContribuyentesDesdeZipTask.Campo.*;
import static java.util.Map.entry;

public class ObtenerContribuyentesDesdeZipTask extends Task<List<Contribuyente>> {

    private final String archivoZip;

    Map<String, String> sustitucionesDirectas =
        Map.<String, String>ofEntries(

            entry("¥", "Ñ"),
            entry(" A?EZ", " ÁÑEZ"),
            entry(" A¿EZ", " ÃÑEZ"),
            entry("?AMANDU", "ÑAMANDÚ"),
            entry("?ANDUTI", "ÑANDUTÍ"),
            entry("?UMBAY", "ÑUMBAY"),
            entry("A?ASCO", "AÑASCO"),
            entry("A?AZCO", "AÑAZCO"),
            entry("A?EZ ROLON", "ÁÑEZ ROLÓN"),
            entry("ACU?A", "ACUÑA"),
            entry("ACU¿A", "ACUÑA"),
            entry("ACU¿¿¿A", "ACUÑA"),
            entry("ACUÃ¿â¿¿A", "ACUÑA"),
            entry("AG?ERO", "AGÜERO"),
            entry("AG¿ERO", "AGÜERO"),
            entry("AG¿¿¿ERO", "AGÜERO"),
            entry("AGÃ¿Â¼ERO", "AGÜERO"),
            entry("AGÃ¿Å¿ERO", "AGÜERO"),
            entry("AG¿ILERA", "AGUILERA"),
            entry("AG¿AYO", "AGUAYO"),
            entry("ALBA¿EZ", "ALBÁÑEZ"),
            entry("ALBARI?O", "ALBARIÑO"),
            entry("ALBARI¿O", "ALBARIÑO"),
            entry("ALBARI¿¿¿O", "ALBARIÑO"),
            entry("ANTU?A", "ANTUÑA"),
            entry("ARG?ELLO", "ARGÜELLO"),
            entry("ARGA?A", "ARGAÑA"),
            entry("ARGA¿A", "ARGAÑA"),
            entry("ARGA¿¿¿A", "ARGAÑA"),
            entry("ARGAÃ¿Â¿¿A", "ARGAÑA"),
            entry("ARGAÃ¿â¿¿A", "ARGAÑA"),
            entry("ARG¿ELLO", "ARGÜELLO"),
            entry("ARG¿¿¿ELLO", "ARGÜELLO"),
            entry("ARGÃ¿Å¿ELLO", "ARGAÑELLO"),
            entry("A¿ASCO", "AÑASCO"),
            entry("A¿AZCO", "AÑAZCO"),
            entry("A¿EZ SANCHEZ", "ÃÑEZ SÁNCHEZ"),
            entry("A¿¿¿ASCO", "AÑASCO"),
            entry("A¿¿¿AZCO", "AÑAZCO"),
            entry("AÃ¿â¿¿AZCO", "AÑAZCO"),
            entry("BABA?OLI", "BABAÑOLI"),
            entry("BABA¿OLI", "BABAÑOLI"),
            entry("BABA¿¿¿OLLI", "BABAÑOLLI"),
            entry("BENDA?A", "BENDAÑA"),
            entry("BOLA?O", "BOLAÑO"),
            entry("BOLA¿O", "BOLAÑO"),
            entry("BOLAÃ¿â¿¿O", "BOLAÑO"),
            entry("BRA?AS", "BRAÑAS"),
            entry("BRIZUE?A", "BRIZUEÑA"),
            entry("BRIZUE¿A", "BRIZUEÑA"),
            entry("BRIZUE¿¿¿A", "BRIZUEÑA"),
            entry("BRIZUEÃ¿â¿¿A", "BRIZUEÑA"),
            entry("BR¿NSTRUP", "BRÖNSTRUP"),
            entry("BURGUE?O", "BURGUEÑO"),
            entry("BURI?IGO", "BURIÑIGO"),
            entry("B¿NDCHEN", "BÜNDCHEN"),
            entry("B¿TTGER", "BÖTTGER"),
            entry("B¿TTNER", "BÖTTNER"),
            entry("CA?ELLA", "CAÑELLA"),
            entry("CA?ELLAS", "CAÑELLAS"),
            entry("CA?ETE", "CAÑETE"),
            entry("CA?ISA", "CAÑISA"),
            entry("CA?IZA", "CAÑIZA"),
            entry("CABA?A", "CABAÑA"),
            entry("CABA¿A", "CABAÑA"),
            entry("CABA¿¿¿A", "CABAÑA"),
            entry("CABAÃ¿ÂÑA", "CABAÑA"),
            entry("CABAÃ¿â¿¿A", "CABAÑA"),
            entry("CAMPA?OLI", "CAMPAÑOLI"),
            entry("CARMI?A", "CARMIÑA"),
            entry("CARMI¿¿¿A", "CARMIÑA"),
            entry("CARRE¿O", "CARREÑO"),
            entry("CASA?AS", "CASAÑAS"),
            entry("CASTA?E", "CASTAÑE"),
            entry("CASTA?O", "CASTAÑO"),
            entry("CASTA¥O", "CASTAÑO"),
            entry("CASTA¿O", "CASTAÑO"),
            entry("CASTAÃ¿â¿¿EYRA", "CASTAÑEYRA"),
            entry("CASTI?EIRA", "CASTIÑEIRA"),
            entry("CASTI¿EIRA", "CASTIÑEIRA"),
            entry("CA¿ELLAS", "CAÑELLAS"),
            entry("CA¿ETE", "CAÑETE"),
            entry("CA¿ISA", "CAÑISA"),
            entry("CA¿IZA", "CAÑIZA"),
            entry("CA¿¿¿ETE", "CAÑETE"),
            entry("CA¿¿¿IZA", "CAÑIZA"),
            entry("CAÃ¿Â¿¿ETE", "CAÑETE"),
            entry("CAÃ¿â¿¿ETE", "CAÑETE"),
            entry("CAÃ¿â¿¿IZA", "CAÑIZA"),
            entry("CHAMORRO A¿AD", "CHAMORRO AÑAD"),
            entry("CHERA?UK", "CHERAÑUK"),
            entry("COUSI?O", "COUSIÑO"),
            entry("COUSI¿O", "COUSIÑO"),
            entry("D?CK", "DÜCK"),
            entry("DAUVI?A", "DAUVIÑA"),
            entry("DO¿ATE", "DOÑATE"),
            entry("DUE¿AS", "DUEÑAS"),
            entry("DUEÃ¿â¿¿AS", "DUEÑAS"),
            entry("DURA?ONA", "DURAÑONA"),
            entry("DURA¿ONA", "DURAÑONA"),
            entry("DURAÃ¿â¿¿ONA", "DURAÑONA"),
            entry("D¿CK", "DÜCK"),
            entry("ECHAG?E", "ECHAGÜE"),
            entry("ECHAG¿E", "ECHAGÜE"),
            entry("ECHAGÃ¿Å¿E", "ECHAGÜE"),
            entry("EG¿EZ", "EGÜEZ"),
            entry("EISENK¿LBL", "EISENKÖLBL"),
            entry("ESPA?A", "ESPAÑA"),
            entry("ESPI?O", "ESPIÑO"),
            entry("ESPI¿O", "ESPIÑO"),
            entry("F?TIMA", "FÁTIMA"),
            entry("FARI?A", "FARIÑA"),
            entry("FARI¿A", "FARIÑA"),
            entry("FARI¿¿¿A", "FARIÑA"),
            entry("FARIÃ¿Â¿¿A", "FARIÑA"),
            entry("FARIÃ¿â¿¿A", "FARIÑA"),
            entry("FLORENCIA?EZ", "FLORENCIÁÑEZ"),
            entry("FLORENCIA¿EZ", "FLORENCIÁÑEZ"),
            entry("FLORENCIA¿¿¿EZ", "FLORENCIÁÑEZ"),
            entry("FLORENCIAÃ¿â¿¿EZ", "FLORENCIÁÑEZ"),
            entry("FL¿CKIGER", "FLÜCKIGER"),
            entry("FRA?O", "FRAÑO"),
            entry("F¿TIMA", "FÁTIMA"),
            entry("G?INEVE", "GÜINEVE"),
            entry("GERRE?O", "GUERREÑO"),
            entry("GORO?OSKI", "GOROÑOSKI"),
            entry("GRA?A", "GRAÑA"),
            entry("GRA¿A", "GRAÑA"),
            entry("GRI?O", "GRIÑO"),
            entry("GR¿BEL", "GRÜBEL"),
            entry("GUERRE?O", "GUERREÑO"),
            entry("GURRE?O", "GUERREÑO"),
            entry("GUERRE¿O", "GUERREÑO"),
            entry("GUERRE¿¿¿O", "GUERREÑO"),
            entry("GUERREÃ¿â¿¿O", "GUERREÑO"),
            entry("G¿CHTER", "GÄCHTER"),
            entry("G¿NTHER", "GÜNTHER"),
            entry("H?THER", "HÜTHER"),
            entry("H?TTEMAN", "HÜTTEMAN"),
            entry("H?TTERMAN", "HÜTTERMAN"),
            entry("H¿BER", "HÜBER"),
            entry("H¿THER", "HÜTHER"),
            entry("H¿TTEMAN", "HÜTTEMAN"),
            entry("H¿TTER", "HÜTTER"),
            entry("H¿TTERMAN", "HÜTTERMAN"),
            entry("I?AKI", "IÑAKI"),
            entry("IBA?EZ", "IBÁÑEZ"),
            entry("IBA¿EZ", "IBÁÑEZ"),
            entry("IBA¿¿¿EZ", "IBÁÑEZ"),
            entry("IBAÃ¿â¿¿EZ", "IBÁÑEZ"),
            entry("IBAÃ¿Â¿¿EZ", "IBÁÑEZ"),
            entry("IVAÃ¿â¿¿EZ", "IVÁÑEZ"),
            entry("JA¿A", "JAÑA"),
            entry("J¿RGEN", "JÜRGEN"),
            entry("K?NG", "KÜNG"),
            entry("KR?GER", "KRÜGER"), // KRÖGER o KRÜGER ?
            entry("KR¿GER", "KRÜGER"), // KRÖGER o KRÜGER ?
            entry("K¿CHENMEISTER", "KÜCHENMEISTER"),
            entry("K¿FER", "KÄFER"),
            entry("K¿HN", "KÜHN"),
            entry("K¿NG", "KÜNG"),
            entry("K¿NZLE", "KÜNZLE"),
            entry("K¿RNER", "KORNER"),
            entry("K¿HLER", "KOHLER"),
            entry("L?WEN", "LÖWEN"),
            entry("LAGRA?A", "LAGRAÑA"),
            entry("LAGRA¿A", "LAGRAÑA"),
            entry("LAGRA¿¿¿A", "LAGRAÑA"),
            entry("LAGRAÃ¿â¿¿A", "LAGRAÑA"),
            entry("LEÃ¿â¿¿N", "LEÓN"),
            entry("LI?AN", "LIÑÁN"),
            entry("LIME?O", "LIMEÑO"),
            entry("LIME¿O", "LIMEÑO"),
            entry("LIME¿¿¿O", "LIMEÑO"),
            entry("LUJ¿¿¿N", "LUJÁN"),
            entry("L¿BEL", "LÖBEL"),
            entry("L¿BLEIN", "LÖBLEIN"),
            entry("L¿HRER", "LÖHRER"),
            entry("L¿WE", "LÖWE"),
            entry("L¿WEN", "LÖWEN"),
            entry("M?LLER", "MÜLLER"),
            entry("MA?OTTI", "MAÑOTTI"),
            entry("MACARE?O", "MACAREÑO"),
            entry("MACAREÃ¿â¿¿O", "MASCAREÑO"),
            entry("MARI?O", "MARIÑO"),
            entry("MARI¿O", "MARIÑO"),
            entry("MARTI¿UK", "MARTIÑUK"),
            entry("MASCARE?O", "MASCAREÑO"),
            entry("MASCARE¿O", "MASCAREÑO"),
            entry("MASCARE¿¿¿O", "MASCAREÑO"),
            entry("MASCAREÃ¿â¿¿O", "MASCAREÑO"),
            entry("MA¿OTTI", "MAÑOTTI"),
            entry("MI?ARRO", "MIÑARRO"),
            entry("MI?O", "MIÑO"),
            entry("MIGUELI?O", "MIGUELIÑO"),
            entry("MI¿ARRO", "MIÑARRO"),
            entry("MI¿O", "MIÑO"),
            entry("MI¿¿¿ARRO", "MIÑARRO"),
            entry("MI¿¿¿O", "MIÑO"),
            entry("MIÃ¿â¿¿ARRO", "MIÑARRO"),
            entry("MIÃ¿â¿¿O", "MIÑO"),
            entry("MONTA?A", "MONTAÑA"),
            entry("MONTA?ES", "MONTAÑES"),
            entry("MONTA?EZ", "MONTÁÑEZ"),
            entry("MONTA¿EZ", "MONTÁÑEZ"),
            entry("MU¿IZ", "MUÑIZ"),
            entry("MU?OZ", "MUÑOZ"),
            entry("MU¿OZ", "MUÑOZ"),
            entry("MU¿¿¿OZ", "MUÑOZ"),
            entry("MUÃ¿â¿¿OZ", "MUÑOZ"),
            entry("M¿LLER", "MÜLLER"),
            entry("MÃ¿Å¿LLER", "MÜLLER"),
            entry("NI?A", "NIÑA"),
            entry("NI?O", "NIÑO"),
            entry("NI¿O", "NIÑO"),
            entry("NI¿¿¿O", "NIÑO"),
            entry("NIÃ¿â¿¿O", "NIÑO"),
            entry("NU?EZ", "NÚÑEZ"),
            entry("NU¿EZ", "NÚÑEZ"),
            entry("NU¿¿¿EZ", "NÚÑEZ"),
            entry("NUÃ¿Â¿¿EZ", "NÚÑEZ"),
            entry("NUÃ¿ÂÑEZ", "NÚÑEZ"),
            entry("NUÃ¿â¿¿EZ", "NÚÑEZ"),
            entry("OCA?O", "OCAÑO"),
            entry("OCA¿A", "OCAÑA"),
            entry("OLE?IK", "OLEÑIK"),
            entry("ORDO?EZ", "ORDÓÑEZ"),
            entry("ORDOÃ¿â¿¿EZ", "ORDÓÑEZ"),
            entry("ORDU?A", "ORDUÑA"),
            entry("ORDU¿A", "ORDUÑA"),
            entry("OTA?O", "OTAÑO"),
            entry("OTA¿O", "OTAÑO"),
            entry("OTA¿¿¿O", "OTAÑO"),
            entry("PATI?O", "PATIÑO"),
            entry("PATI¿O", "PATIÑO"),
            entry("PATI¿¿¿O", "PATIÑO"),
            entry("PATIÃ¿Â¿¿O", "PATIÑO"),
            entry("PATIÃ¿â¿¿O", "PATIÑO"),
            entry("PAÃ¿â¿¿ANES", "PAÑANES"),
            entry("PE?A", "PEÑA"),
            entry("PE¿A", "PEÑA"),
            entry("PE¿¿¿A", "PEÑA"),
            entry("PEÃ¿ÂÑA", "PEÑA"),
            entry("PEÃ¿â¿¿A", "PEÑA"),
            entry("PI?ANEZ", "PIÑÁNEZ"),
            entry("PI?EIRO", "PIÑEIRO"),
            entry("PI¿ANEZ", "PIÑÁNEZ"),
            entry("PI¿EIRO", "PIÑEIRO"),
            entry("PI¿¿¿ANEZ", "PIÑÁNEZ"),
            entry("PI¿¿¿EIRO", "PIÑEIRO"),
            entry("PIÃ¿ÂÑANEZ", "PIÑÁNEZ"),
            entry("PIÃ¿â¿¿ANEZ", "PIÑÁNEZ"),
            entry("QUI?ONES", "QUIÑONES"),
            entry("QUI?ONEZ", "QUIÑÓNEZ"),
            entry("QUI¿ONEZ", "QUIÑÓNEZ"),
            entry("QUI¿¿¿ONEZ", "QUIÑÓNEZ"),
            entry("QUIÃ¿â¿¿ONEZ", "QUIÑÓNEZ"),
            entry("QUIÃ¿Â¿¿ONEZ", "QUIÑÓNEZ"),
            entry("R?HRIG", "RÖHRIG"),
            entry("RAMÃ¿â¿¿N", "RAMÓN"),
            entry("RA¿L", "RAÚL"),
            entry("ROC¿O", "ROCÍO"),
            entry("RODI?O", "RODIÑO"),
            entry("ROMA?ACH", "ROMAÑACH"),
            entry("ROMA?UK", "ROMAÑUK"),
            entry("ROMA¿ACH", "ROMAÑACH"),
            entry("R¿HRIG", "RÖHRIG"),
            entry("R¿NNEBECK", "RÖNNEBECK"),
            entry("R¿PKE", "RÖPKE"),
            entry("SALDA?A", "SALDAÑA"),
            entry("SALDA¿A", "SALDAÑA"),
            entry("SCH?LLER", "SCHÖLLER"),
            entry("SCH¿FER", "SCHÄFER"),
            entry("SCH¿FFER", "SCHÄFFER"),
            entry("SCH¿LLER", "SCHÖLLER"),
            entry("SCH¿TZ", "SCHÜTZ"),
            entry("SCH¿¿¿LLER", "SCHÖLLER"),
            entry("SE¿OR", "SEÑOR"),
            entry("SOPE?A", "SOPEÑA"),
            entry("S¿FSTRAND", "SAFSTRAND"),
            entry("TEL?KEN", "TELÖKEN"),
            entry("TEL¿KEN", "TELÖKEN"),
            entry("TO?ANES", "TOÑANES"),
            entry("TO?ANEZ", "TOÑÁNEZ"),
            entry("TORI¿O", "TORIÑO"),
            entry("TO¿ANEZ", "TOÑÁNEZ"),
            entry("TO¿¿¿ANEZ", "TOÑÁNEZ"),
            entry("TOÃ¿â¿¿ANEZ", "TOÑÁNEZ"),
            entry("VI?ALES", "VIÑALES"),
            entry("VI?ALS", "VIÑALS"),
            entry("VI?ARRO", "VIÑARRO"),
            entry("VILLAFA?E", "VILLAFAÑE"),
            entry("VI¿ALES", "VIÑALES"),
            entry("VI¿ARRO", "VIÑARRO"),
            entry("VIÃ¿â¿¿ALES", "VIÑALES"),
            entry("VON L¿CKEN", "VON LÜCKEN"),
            entry("V¿MEL", "VÖMEL"),
            entry("WIESENH?TTER", "WIESENHÜTTER"),
            entry("WIESENH¿TER", "WIESENHÜTER"),
            entry("WIESENH¿TTER", "WIESENHÜTTER"),
            entry("YA?EZ", "YÁÑEZ"),
            entry("YA¿EZ", "YÁÑEZ"),
            entry("YA?IZ", "YAÑIZ"),
            entry("YBA?EZ", "YBÁÑEZ"),
            entry("YBA¿EZ", "YBÁÑEZ"),
            entry("YBA¿¿¿EZ", "YBÁÑEZ"),
            entry("ZU?IGA", "ZÚÑIGA"),
            entry("ZU¿IGA", "ZÚÑIGA"),
            entry("¿AMANDU", "ÑAMANDÚ"),
            entry("¿ARI BAEZ,", "ÑARÍ BÁEZ,"),
            entry("¿UMBAY", "ÑUMBAY"),
            entry("Ã¿â¿¿ARIZ", "ÑARIZ"),
            entry("Ã¿â¿ÑAMANDÚ", "ÑAMANDÚ"),
            entry("Ã¿â¿¿AMANDU", "ÑAMANDÚ"),
            entry(" .S.A", " S.A.")

            // entry("SA", "S.A.")


        );


    Map<Pattern, String> sustitucionesRegex =
        Map.<Pattern, String>ofEntries(
            entry(Pattern.compile("\\bANTUNEZ\\b"), "ANTÚNEZ"),
            entry(Pattern.compile("\\bLEON\\b"), "LEÓN"),
            entry(Pattern.compile("\\bACHON\\b"), "ACHÓN"),
            entry(Pattern.compile("\\bAGUERO\\b"), "AGÜERO"),
            entry(Pattern.compile("\\bALARCON\\b"), "ALARCÓN"),
            entry(Pattern.compile("\\bALMIRON\\b"), "ALMIRÓN"),
            entry(Pattern.compile("\\bALVAREZ\\b"), "ÁLVAREZ"),
            entry(Pattern.compile("\\bAÑEZ\\b"), "ÁÑEZ"),
            entry(Pattern.compile("\\bARAUJO\\b"), "ARAÚJO"),
            entry(Pattern.compile("\\bAREVALO\\b"), "ARÉVALO"),
            entry(Pattern.compile("\\bAREVALOS\\b"), "ARÉVALOS"),
            entry(Pattern.compile("\\bARGUELLO\\b"), "ARGÜELLO"),
            entry(Pattern.compile("\\bARRUA\\b"), "ARRÚA"),
            entry(Pattern.compile("\\bAVALO\\b"), "ÁVALO"),
            entry(Pattern.compile("\\bAVALOS\\b"), "ÁVALOS"),
            entry(Pattern.compile("\\bBAEZ\\b"), "BÁEZ"),
            entry(Pattern.compile("\\bBARUA\\b"), "BARÚA"),
            entry(Pattern.compile("\\bBELTRAN\\b"), "BELTRÁN"),
            entry(Pattern.compile("\\bBENITEZ\\b"), "BENÍTEZ"),
            entry(Pattern.compile("\\bBOGARIN\\b"), "BOGARÍN"),
            entry(Pattern.compile("\\bBORDON\\b"), "BORDÓN"),
            entry(Pattern.compile("\\bBRITEZ\\b"), "BRÍTEZ"),
            entry(Pattern.compile("\\bCACERES\\b"), "CÁCERES"),
            entry(Pattern.compile("\\bCALDERON\\b"), "CALDERÓN"),
            entry(Pattern.compile("\\bCATALAN\\b"), "CATALÁN"),
            entry(Pattern.compile("\\bCENTURION\\b"), "CENTURIÓN"),
            entry(Pattern.compile("\\bCESPEDES\\b"), "CÉSPEDES"),
            entry(Pattern.compile("\\bCHAVEZ\\b"), "CHÁVEZ"),
            entry(Pattern.compile("\\bCOLMAN\\b"), "COLMÁN"),
            entry(Pattern.compile("\\bCORDOBA\\b"), "CÓRDOBA"),
            entry(Pattern.compile("\\bDAVALOS\\b"), "DÁVALOS"),
            entry(Pattern.compile("\\bDELEON\\b"), "DELEÓN"),
            entry(Pattern.compile("\\bDIAZ\\b"), "DÍAZ"),
            entry(Pattern.compile("\\bDOLDAN\\b"), "DOLDÁN"),
            entry(Pattern.compile("\\bDOMINGUEZ\\b"), "DOMÍNGUEZ"),
            entry(Pattern.compile("\\bDURE\\b"), "DURÉ"),
            entry(Pattern.compile("\\bECHAGUE\\b"), "ECHAGÜE"),
            entry(Pattern.compile("\\bECHEVERRIA\\b"), "ECHEVERRÍA"),
            entry(Pattern.compile("\\bESPINOLA\\b"), "ESPÍNOLA"),
            entry(Pattern.compile("\\bFALCON\\b"), "FALCÓN"),
            entry(Pattern.compile("\\bFERNANDEZ\\b"), "FERNÁNDEZ"),
            entry(Pattern.compile("\\bFELICIANGELI\\b"), "FELICIÁNGELI"),
            entry(Pattern.compile("\\bFILARTIGA\\b"), "FILÁRTIGA"),
            entry(Pattern.compile("\\bFORNERON\\b"), "FORNERÓN"),
            entry(Pattern.compile("\\bFLORENTIN\\b"), "FLORENTÍN"),
            entry(Pattern.compile("\\bGAMON\\b"), "GAMÓN"),
            entry(Pattern.compile("\\bGARCIA\\b"), "GARCÍA"),
            entry(Pattern.compile("\\bGARANTIA\\b"), "GARANTÍA"),
            entry(Pattern.compile("\\bGAVILAN\\b"), "GAVILÁN"),
            entry(Pattern.compile("\\bGIMENEZ\\b"), "GIMÉNEZ"),
            entry(Pattern.compile("\\bGOIBURU\\b"), "GOIBURÚ"),
            entry(Pattern.compile("\\bGOMEZ\\b"), "GÓMEZ"),
            entry(Pattern.compile("\\bGONZALEZ\\b"), "GONZÁLEZ"),
            entry(Pattern.compile("\\bGUILLEN\\b"), "GUILLÉN"),
            entry(Pattern.compile("\\bGUTIERREZ\\b"), "GUTIÉRREZ"),
            entry(Pattern.compile("\\bGUZMAN\\b"), "GUZMÁN"),
            entry(Pattern.compile("\\bIBAÑEZ\\b"), "IBÁÑEZ"),
            entry(Pattern.compile("\\bINSFRAN\\b"), "INSFRÁN"),
            entry(Pattern.compile("\\bIRRAZABAL\\b"), "IRRAZÁBAL"),
            entry(Pattern.compile("\\bJESUS\\b"), "JESÚS"),
            entry(Pattern.compile("\\bJIMENEZ\\b"), "JIMÉNEZ"),
            entry(Pattern.compile("\\bLEGUIZAMON\\b"), "LEGUIZAMÓN"),
            entry(Pattern.compile("\\bLOPEZ\\b"), "LÓPEZ"),
            entry(Pattern.compile("\\bLUJAN\\b"), "LUJÁN"),
            entry(Pattern.compile("\\bMARTINEZ\\b"), "MARTÍNEZ"),
            entry(Pattern.compile("\\bMARMOL\\b"), "MÁRMOL"),
            entry(Pattern.compile("\\bMENDEZ\\b"), "MÉNDEZ"),
            entry(Pattern.compile("\\bMONGELOS\\b"), "MONGELÓS"),
            entry(Pattern.compile("\\bMONTANIA\\b"), "MONTANÍA"),
            entry(Pattern.compile("\\bMONZON\\b"), "MONZÓN"),
            entry(Pattern.compile("\\bMORAN\\b"), "MORÁN"),
            entry(Pattern.compile("\\bMORINIGO\\b"), "MORÍNIGO"),
            entry(Pattern.compile("\\bNARVAEZ\\b"), "NARVÁEZ"),
            entry(Pattern.compile("\\bNUÑEZ\\b"), "NÚÑEZ"),
            entry(Pattern.compile("\\bOBREGON\\b"), "OBREGÓN"),
            entry(Pattern.compile("\\bORDOÑEZ\\b"), "ORDÓÑEZ"),
            entry(Pattern.compile("\\bORUE\\b"), "ORUÉ"),
            entry(Pattern.compile("\\bOTAZU\\b"), "OTAZÚ"),
            entry(Pattern.compile("\\bPAEZ\\b"), "PÁEZ"),
            entry(Pattern.compile("\\bPARANA\\b"), "PARANÁ"),
            entry(Pattern.compile("\\bPAVON\\b"), "PAVÓN"),
            entry(Pattern.compile("\\bPEREZ\\b"), "PÉREZ"),
            entry(Pattern.compile("\\bRAMIREZ\\b"), "RAMÍREZ"),
            entry(Pattern.compile("\\bRIOS\\b"), "RÍOS"),
            entry(Pattern.compile("\\bRODRIGUEZ\\b"), "RODRÍGUEZ"),
            entry(Pattern.compile("\\bROLON\\b"), "ROLÓN"),
            entry(Pattern.compile("\\bRUIZ DIAZ\\b"), "RUIZ DÍAZ"),
            entry(Pattern.compile("\\bSANCHEZ\\b"), "SÁNCHEZ"),
            entry(Pattern.compile("\\bSERVIAN\\b"), "SERVIÁN"),
            entry(Pattern.compile("\\bSERVIN\\b"), "SERVÍN"),
            entry(Pattern.compile("\\bSUAREZ\\b"), "SUÁREZ"),
            entry(Pattern.compile("\\bTILLERIA\\b"), "TILLERÍA"),
            entry(Pattern.compile("\\bVAZQUEZ\\b"), "VÁZQUEZ"),
            entry(Pattern.compile("\\bVELAZQUEZ\\b"), "VELÁZQUEZ"),
            entry(Pattern.compile("\\bVERDUN\\b"), "VERDÚN"),
            entry(Pattern.compile("\\bVERON\\b"), "VERÓN"),
            entry(Pattern.compile("\\bVICENTIN\\b"), "VICENTÍN"),
            entry(Pattern.compile("\\bYNSFRAN\\b"), "YNSFRÁN"),
            entry(Pattern.compile("\\bZACARIAS\\b"), "ZACARÍAS"),
            entry(Pattern.compile("\\bZALDIVAR\\b"), "ZALDÍVAR"),
            entry(Pattern.compile("\\bZARATE\\b"), "ZÁRATE"),
            entry(Pattern.compile("\\bZUBELDIA\\b"), "ZUBELDÍA")

        );

    //private final Pattern patternPersonaFisica = Pattern.compile("^[\\p{L}]+( [\\p{L}]+)*,[ ]+[\\p{L}]+( [\\p{L}]+)*$");
    Map<Pattern, String> sustitucionesPersJurid = Map.<Pattern, String>ofEntries(

        entry(Pattern.compile("\\bADMINISTRACION\\b"), "ADMINISTRACIÓN"),
        entry(Pattern.compile("\\bSOCIEDADANONIMA\\b"), "SOCIEDAD ANÓNIMA"),
        entry(Pattern.compile("\\bSOCIEDAD ANONIMAS\\b"), "SOCIEDAD ANÓNIMA"),
        entry(Pattern.compile("\\bANONIMASUCURSAL\\b"), "ANÓNIMA SUCURSAL"),
        entry(Pattern.compile("\\bANONIMA\\b"), "ANÓNIMA"),
        entry(Pattern.compile("\\bAANONIMA\\b"), "ANÓNIMA"),
        entry(Pattern.compile("\\bAGIL\\b"), "ÁGIL"),
        entry(Pattern.compile("\\bANONIMO\\b"), "ANÓNIMO"),
        entry(Pattern.compile("\\bALCOHOLICO"), "ALCOHÓLICO"),
        entry(Pattern.compile("\\bASESORIA\\b"), "ASESORÍA"),
        entry(Pattern.compile("\\bSEMILLERIA\\b"), "SEMILLERÍA"),
        entry(Pattern.compile("\\bORGANIZACION\\b"), "ORGANIZACIÓN"),
        entry(Pattern.compile("\\bCOOPERACION\\b"), "COOPERACIÓN"),
        entry(Pattern.compile("\\bCOPERACION\\b"), "COOPERACIÓN"),
        entry(Pattern.compile("\\bUNION\\b"), "UNIÓN"),
        entry(Pattern.compile("\\bBASICA\\b"), "BÁSICA"),
        entry(Pattern.compile("\\bBASICO\\b"), "BÁSICO"),
        entry(Pattern.compile("\\bGESTION\\b"), "GESTIÓN"),
        entry(Pattern.compile("\\bCREDITO\\b"), "CRÉDITO"),
        entry(Pattern.compile("\\bARTESANIA"), "ARTESANÍA"),
        entry(Pattern.compile("\\bINFORMATICA"), "INFORMÁTICA"),
        entry(Pattern.compile("\\bINFORMATICO"), "INFORMÁTICO"),
        entry(Pattern.compile("\\bELECTRONICA"), "ELECTRÓNICA"),
        entry(Pattern.compile("\\bELECTRONICO"), "ELECTRÓNICO"),
        entry(Pattern.compile("\\bMECATRONICO"), "MECATRONICO"),
        entry(Pattern.compile("\\bFARMACEUTIC"), "FARMACÉUTIC"),
        entry(Pattern.compile("\\bSESAMO\\b"), "SÉSAMO"),
        entry(Pattern.compile("\\bMASONICA"), "MASÓNICA"),
        entry(Pattern.compile("\\bSIMBOLICA"), "SIMBÓLICA"),
        entry(Pattern.compile("\\bCOMITE\\b"), "COMITÉ"),
        entry(Pattern.compile("\\bCOMITES\\b"), "COMITÉS"),
        entry(Pattern.compile("\\bJOVENES\\b"), "JÓVENES"),
        entry(Pattern.compile("\\bINDIGENA"), "INDÍGENA"),
        entry(Pattern.compile("\\bPUBLICO\\b"), "PÚBLICO"),
        entry(Pattern.compile("\\bPUBLICOS\\b"), "PÚBLICOS"),
        entry(Pattern.compile("\\bPUBLICAS\\b"), "PÚBLICAS"),
        entry(Pattern.compile("\\bPUBLICA\\b"), "PÚBLICA"), // ciones
        entry(Pattern.compile("\\bCOMISION\\b"), "COMISIÓN"),
        entry(Pattern.compile("\\bCOMPAÑIA\\b"), "COMPAÑÍA"),
        entry(Pattern.compile("\\bDESTILERIA\\b"), "DESTILERÍA"),
        entry(Pattern.compile("\\bCAMARA\\b"), "CÁMARA"),
        entry(Pattern.compile("\\bDIAGNOSTICO"), "DIAGNÓSTICO"),
        entry(Pattern.compile("\\bMARIAS\\b"), "MARÍAS"),
        entry(Pattern.compile("\\bEXOTICOS\\b"), "EXÓTICOS"),
        entry(Pattern.compile("\\bÑANDUTI\\b"), "ÑANDUTÍ"),
        entry(Pattern.compile("\\bPANAMBI\\b"), "PANAMBÍ"),
        entry(Pattern.compile("\\bPRINCIPE\\b"), "PRÍNCIPE"),
        entry(Pattern.compile("\\bGUASU\\b"), "GUASÚ"),
        entry(Pattern.compile("\\bGUAZU\\b"), "GUAZÚ"),
        entry(Pattern.compile("\\bTAPE\\b"), "TAPÉ"),
        entry(Pattern.compile("\\bCOMPAÑIA\\b"), "COMPAÑÍA"),
        entry(Pattern.compile("\\bINGENIERIA\\b"), "INGENIERÍA"),
        entry(Pattern.compile("\\bMETALURGICA\\b"), "METALÚRGICA"),
        entry(Pattern.compile("\\bLOGISTICA\\b"), "LOGÍSTICA"),
        entry(Pattern.compile("\\bFERRETERIA"), "FERRETERÍA"),
        entry(Pattern.compile("\\bESCRIBANIA"), "ESCRIBANÍA"),
        entry(Pattern.compile("\\bJURIDICA"), "JURÍDICA"),
        entry(Pattern.compile("\\bCONSULTORIA"), "CONSULTORÍA"),
        entry(Pattern.compile("\\bESTRATEGICO"), "ESTRATÉGICO"),
        entry(Pattern.compile("\\bESTRATEGICA"), "ESTRATÉGICA"),
        entry(Pattern.compile("\\bECONOMICA"), "ECONÓMICA"),
        entry(Pattern.compile("\\bTRANSITO"), "TRÁNSITO"),
        entry(Pattern.compile("TECNICA\\b"), "TÉCNICA"),
        entry(Pattern.compile("TECNICAS\\b"), "TÉCNICAS"),
        entry(Pattern.compile("TECNICO\\b"), "TÉCNICO"),
        entry(Pattern.compile("CUPULA\\b"), "CÚPULA"),
        entry(Pattern.compile("ATLETICO\\b"), "ATLÉTICO"),
        entry(Pattern.compile("TECNICOS\\b"), "TÉCNICOS"),
        entry(Pattern.compile("TECNOLOGIA\\b"), "TECNOLOGÍA"),
        entry(Pattern.compile("TEGNOLOGIA\\b"), "TECNOLOGÍA"),
        entry(Pattern.compile("TECNOLOGIAS\\b"), "TECNOLOGÍAS"),
        entry(Pattern.compile("\\bTRIANGULO\\b"), "TRIÁNGULO"),
        entry(Pattern.compile("LOGICAS\\b"), "LÓGICAS"),
        entry(Pattern.compile("LOGICA\\b"), "LÓGICA"),
        entry(Pattern.compile("LOGICOS\\b"), "LÓGICOS"),
        entry(Pattern.compile("LOGICO\\b"), "LÓGICO"),
        entry(Pattern.compile("\\bMEDICA\\b"), "MÉDICA"),
        entry(Pattern.compile("\\bMEDICO\\b"), "MÉDICO"),
        entry(Pattern.compile("\\bMEDICOS\\b"), "MÉDICOS"),
        entry(Pattern.compile("CIRUGIA"), "CIRUGÍA"),
        entry(Pattern.compile("\\bCRANEO\\b"), "CRÁNEO"),
        entry(Pattern.compile("OPTICA\\b"), "ÓPTICA"),
        entry(Pattern.compile("OPTICAS\\b"), "ÓPTICAS"),
        entry(Pattern.compile("OPTICO\\b"), "ÓPTICO"),
        entry(Pattern.compile("OPTICOS\\b"), "ÓPTICOS"),
        entry(Pattern.compile("\\bREGION\\b"), "REGIÓN"),
        entry(Pattern.compile("\\bPATRIOTICA\\b"), "PATRIÓTICA"),
        entry(Pattern.compile("\\bPATRIOTICO\\b"), "PATRIÓTICO"),
        entry(Pattern.compile("\\bBIOTICO\\b"), "BIÓTICO"),
        entry(Pattern.compile("\\bROBOTICA\\b"), "ROBÓTICA"),
        entry(Pattern.compile("\\bCONVENCION\\b"), "CONVENCIÓN"),
        entry(Pattern.compile("\\bCOMIOSION\\b"), "COMISIÓN"),
        entry(Pattern.compile("\\bCOMOSION\\b"), "COMISIÓN"),
        entry(Pattern.compile("\\bSUBCOMISION\\b"), "SUBCOMISIÓN"),
        entry(Pattern.compile("\\bSUCOMOSION\\b"), "SUBCOMISIÓN"),
        entry(Pattern.compile("\\bSUBCOMOSION\\b"), "SUBCOMISIÓN"),
        entry(Pattern.compile("\\bTELEVISION\\b"), "TELEVISIÓN"),
        entry(Pattern.compile("\\bCAPITAN\\b"), "CAPITÁN"),
        entry(Pattern.compile("\\bCERAMICA\\b"), "CERÁMICA"),
        entry(Pattern.compile("\\bALTO VERA\\b"), "ALTO VERÁ"),
        entry(Pattern.compile("\\bITA PYTA\\b"), "ITÁ PYTÃ"),
        entry(Pattern.compile("\\bITA YBATE\\b"), "ITÁ YBATÉ"),
        entry(Pattern.compile("\\bISLA PUCU\\b"), "ISLA PUCÚ"),
        entry(Pattern.compile("\\bCERRO CORA\\b"), "CERRO CORÁ"),
        entry(Pattern.compile("MOVILES\\b"), "MÓVILES"),
        entry(Pattern.compile("CLINICAS\\b"), "CLÍNICAS"),
        entry(Pattern.compile("CLINICA\\b"), "CLÍNICA"),
        entry(Pattern.compile("ESTETICA\\b"), "ESTÉTICA"),
        entry(Pattern.compile("PLASTICA\\b"), "PLÁSTICA"),
        entry(Pattern.compile("PLASTICAS\\b"), "PLÁSTICAS"),
        entry(Pattern.compile("PLASTICO\\b"), "PLÁSTICO"),
        entry(Pattern.compile("PLASTICOS\\b"), "PLÁSTICOS"),
        entry(Pattern.compile("\\bMINERIA\\b"), "MINERÍA"),
        entry(Pattern.compile("\\bMINERIAS\\b"), "MINERÍAS"),
        entry(Pattern.compile("\\bMUSICA\\b"), "MÚSICA"),
        entry(Pattern.compile("\\bPASION\\b"), "PASIÓN"),
        entry(Pattern.compile("\\bPRECISION\\b"), "PRECISIÓN"),
        entry(Pattern.compile("\\bOCASION\\b"), "OCASIÓN"),
        entry(Pattern.compile("\\bMISION\\b"), "MISIÓN"),
        entry(Pattern.compile("\\bFUSION\\b"), "FUSIÓN"),
        entry(Pattern.compile("\\bINVERSION\\b"), "INVERSIÓN"),
        entry(Pattern.compile("SUCESION\\b"), "SUCESIÓN"),
        entry(Pattern.compile("\\bCESION\\b"), "CESIÓN"),
        entry(Pattern.compile("\\bPREVISION\\b"), "PREVISIÓN"),
        entry(Pattern.compile("\\bASOCIASION\\b"), "ASOCIACIÓN"),
        entry(Pattern.compile("\\bASOCIACION\\b"), "ASOCIACIÓN"),
        entry(Pattern.compile("\\bFUNDACION\\b"), "FUNDACIÓN"),
        entry(Pattern.compile("\\bGARANTIA\\b"), "GARANTÍA"),
        entry(Pattern.compile("HISTORICO\\b"), "HISTÓRICO"),
        entry(Pattern.compile("HISTORICOS\\b"), "HISTÓRICOS"),
        entry(Pattern.compile("NAUTICA\\b"), "NÁUTICA"),
        entry(Pattern.compile("NAUTICAS\\b"), "NÁUTICAS"),
        entry(Pattern.compile("NAUTICO\\b"), "NÁUTICO"),
        entry(Pattern.compile("NAUTICOS\\b"), "NÁUTICOS"),
        entry(Pattern.compile("GRAFICOS\\b"), "GRÁFICOS"),
        entry(Pattern.compile("GRAFICAS\\b"), "GRÁFICAS"),
        entry(Pattern.compile("GRAFICO\\b"), "GRÁFICO"),
        entry(Pattern.compile("GRAFICA\\b"), "GRÁFICA")
    );


    Map<Pattern, String> sustitucionesNombresRegex = Map.<Pattern, String>ofEntries(

        entry(Pattern.compile("CION\\b"), "CIÓN"),
        //entry(Pattern.compile("\\bMARTIN\\b"), "MARTÍN"),
        entry(Pattern.compile("\\bADRIAN\\b"), "ADRIÁN"),
        entry(Pattern.compile("\\bALVARO\\b"), "ÁLVARO"),
        entry(Pattern.compile("\\bAGUSTIN\\b"), "AGUSTÍN"),
        entry(Pattern.compile("\\bAMERICO\\b"), "AMÉRICO"),
        entry(Pattern.compile("\\bAMBAR\\b"), "ÁMBAR"),
        entry(Pattern.compile("\\bANAHI\\b"), "ANAHÍ"),
        entry(Pattern.compile("\\bANALIA\\b"), "ANALÍA"),
        entry(Pattern.compile("\\bANANIAS\\b"), "ANANÍAS"),
        entry(Pattern.compile("\\bANDRES\\b"), "ANDRÉS"),
        entry(Pattern.compile("\\bANDRONICO\\b"), "ANDRÓNICO"),
        entry(Pattern.compile("\\bANGELES\\b"), "ÁNGELES"),
        entry(Pattern.compile("\\bANGELICA\\b"), "ANGÉLICA"),
        entry(Pattern.compile("\\bANGELO\\b"), "ÁNGELO"),
        entry(Pattern.compile("\\bANGELA\\b"), "ÁNGELA"),
        entry(Pattern.compile("\\bANGEL\\b"), "ÁNGEL"),
        entry(Pattern.compile("\\bANIBAL\\b"), "ANÍBAL"),
        entry(Pattern.compile("\\bANTOLIN\\b"), "ANTOLÍN"),
        entry(Pattern.compile("\\bASCENSION\\b"), "ASCENSIÓN"),
        entry(Pattern.compile("\\bARBOL\\b"), "ÁRBOL"),
        entry(Pattern.compile("\\bARISTIDES\\b"), "ARÍSTIDES"),
        entry(Pattern.compile("\\bARISTOBULO\\b"), "ARISTÓBULO"),
        entry(Pattern.compile("\\bASUNCION\\b"), "ASUNCIÓN"),
        entry(Pattern.compile("\\bBARTOLOME\\b"), "BARTOLOMÉ"),
        entry(Pattern.compile("\\bBELEN\\b"), "BELÉN"),
        entry(Pattern.compile("\\bBENJAMIN\\b"), "BENJAMÍN"),
        entry(Pattern.compile("\\bBLASIDA\\b"), "BLÁSIDA"),
        entry(Pattern.compile("\\bBRIGIDA\\b"), "BRÍGIDA"),
        entry(Pattern.compile("\\bBRIGIDO\\b"), "BRÍGIDO"),
        entry(Pattern.compile("\\bCESAR\\b"), "CÉSAR"),
        entry(Pattern.compile("\\bCONCEPCION\\b"), "CONCEPCIÓN"),
        entry(Pattern.compile("\\bCORAZON\\b"), "CORAZÓN"),
        entry(Pattern.compile("\\bCRISPIN\\b"), "CRISPÍN"),
        entry(Pattern.compile("\\bCRISPULO\\b"), "CRÍSPULO"),
        entry(Pattern.compile("\\bCRISTOBAL\\b"), "CRISTÓBAL"),
        entry(Pattern.compile("\\bDAMIAN\\b"), "DAMIÁN"),
        entry(Pattern.compile("\\bDARIO\\b"), "DARÍO"),
        entry(Pattern.compile("\\bDEBORA\\b"), "DÉBORA"),
        entry(Pattern.compile("\\bDEJESUS\\b"), "DEJESÚS"),
        entry(Pattern.compile("\\bDELFIN\\b"), "DELFÍN"),
        entry(Pattern.compile("\\bELIAS\\b"), "ELÍAS"),
        entry(Pattern.compile("\\bEMERITO\\b"), "EMÉRITO"),
        entry(Pattern.compile("\\bENCARNACION\\b"), "ENCARNACIÓN"),
        entry(Pattern.compile("\\bEFREN\\b"), "EFRÉN"),
        entry(Pattern.compile("\\bESMERITO\\b"), "ESMÉRITO"),
        entry(Pattern.compile("\\bESTEFANIA\\b"), "ESTEFANÍA"),
        entry(Pattern.compile("\\bFABIAN\\b"), "FABIÁN"),
        entry(Pattern.compile("\\bFILEMON\\b"), "FILEMÓN"),
        entry(Pattern.compile("\\bFATIMA\\b"), "FÁTIMA"),
        entry(Pattern.compile("\\bFELIX\\b"), "FÉLIX"),
        entry(Pattern.compile("\\bFERMIN\\b"), "FERMÍN"),
        entry(Pattern.compile("\\bGERONIMO\\b"), "GERÓNIMO"),
        entry(Pattern.compile("\\bGASTON\\b"), "GASTÓN"),
        entry(Pattern.compile("\\bHECTOR\\b"), "HÉCTOR"),
        entry(Pattern.compile("\\bHERNAN\\b"), "HERNÁN"),
        entry(Pattern.compile("\\bHIPOLITO\\b"), "HIPÓLITO"),
        entry(Pattern.compile("\\bHILARION\\b"), "HILARIÓN"),
        entry(Pattern.compile("\\bINES\\b"), "INÉS"),
        entry(Pattern.compile("\\bISAÍAS\\b"), "ISAÍAS"),
        entry(Pattern.compile("\\bIVAN\\b"), "IVÁN"),
        entry(Pattern.compile("\\bJAZMIN\\b"), "JAZMÍN"),
        entry(Pattern.compile("\\bJEREMIAS\\b"), "JEREMÍAS"),
        entry(Pattern.compile("\\bJOAQUIN\\b"), "JOAQUÍN"),
        entry(Pattern.compile("\\bJOSE\\b"), "JOSÉ"),
        entry(Pattern.compile("\\bJOSUE\\b"), "JOSUÉ"),
        entry(Pattern.compile("\\bJOSIAS\\b"), "JOSÍAS"),
        entry(Pattern.compile("\\bJULIAN\\b"), "JULIÁN"),
        entry(Pattern.compile("\\bLIDER\\b"), "LÍDER"),
        entry(Pattern.compile("\\bLEONIDA\\b"), "LEÓNIDA"),
        entry(Pattern.compile("\\bLEONIDO\\b"), "LEÓNIDO"),
        entry(Pattern.compile("\\bLUCIA\\b"), "LUCÍA"),
        entry(Pattern.compile("\\bLUCIDA\\b"), "LÚCIDA"),
        entry(Pattern.compile("\\bLUCIDO\\b"), "LÚCIDO"),
        entry(Pattern.compile("\\bMAGALI\\b"), "MAGALÍ"),
        entry(Pattern.compile("\\bMARIA\\b"), "MARÍA"),
        entry(Pattern.compile("\\bMATIAS\\b"), "MATÍAS"),
        entry(Pattern.compile("\\bMAXIMA\\b"), "MÁXIMA"),
        entry(Pattern.compile("\\bMAXIMO\\b"), "MÁXIMO"),
        entry(Pattern.compile("\\bMOISES\\b"), "MOISÉS"),
        entry(Pattern.compile("\\bMONICA\\b"), "MÓNICA"),
        entry(Pattern.compile("\\bNELIDA\\b"), "NÉLIDA"),
        entry(Pattern.compile("\\bNESTOR\\b"), "NÉSTOR"),
        entry(Pattern.compile("\\bNICOLAS\\b"), "NICOLÁS"),
        entry(Pattern.compile("\\bNOEMI\\b"), "NOEMÍ"),
        entry(Pattern.compile("\\bOSCAR\\b"), "ÓSCAR"),
        entry(Pattern.compile("\\bPANFILO\\b"), "PÁNFILO"),
        entry(Pattern.compile("\\bPLACIDA\\b"), "PLÁCIDA"),
        entry(Pattern.compile("\\bPLACIDO\\b"), "PLÁCIDO"),
        entry(Pattern.compile("\\bRAMON\\b"), "RAMÓN"),
        entry(Pattern.compile("\\bRAUL\\b"), "RAÚL"),
        entry(Pattern.compile("\\bRENE\\b"), "RENÉ"),
        entry(Pattern.compile("\\bROCIO\\b"), "ROCÍO"),
        entry(Pattern.compile("\\bROSALIA\\b"), "ROSALÍA"),
        entry(Pattern.compile("\\bRUBEN\\b"), "RUBÉN"),
        entry(Pattern.compile("\\bSEBASTIAN\\b"), "SEBASTIÁN"),
        entry(Pattern.compile("\\bSENEN\\b"), "SENÉN"),
        entry(Pattern.compile("\\bSENON\\b"), "SENÓN"),
        entry(Pattern.compile("\\bSERAFIN\\b"), "SERAFÍN"),
        entry(Pattern.compile("\\bSIMEON\\b"), "SIMEÓN"),
        entry(Pattern.compile("\\bSOFIA\\b"), "SOFÍA"),
        entry(Pattern.compile("\\bTEOFILO\\b"), "TEÓFILO"),
        entry(Pattern.compile("\\bTOMAS\\b"), "TOMÁS"),
        entry(Pattern.compile("\\bVALENTIN\\b"), "VALENTÍN"),
        entry(Pattern.compile("\\bVERONICA\\b"), "VERÓNICA"),
        entry(Pattern.compile("\\bVICTOR\\b"), "VÍCTOR"),
        entry(Pattern.compile("\\bZENON\\b"), "ZENÓN")
    );

    private final Pattern patronHttpsSustitucion = Pattern.compile("\\s*(http|https)://\\S+", Pattern.CASE_INSENSITIVE);
    // Pattern un poco mas barato para evaluación en loop con muchas comparaciones
    private final Pattern patronHttps = Pattern.compile("http",Pattern.CASE_INSENSITIVE);
    private final Pattern patronIcola = Pattern.compile(".*([RTVPC])ICOLA.*");

    Map<Pattern, String> sustitucionesIcola = Map.ofEntries(
        entry(Pattern.compile("AGRICOLA"), "AGRÍCOLA"),
        entry(Pattern.compile("AVICOLA"), "AVÍCOLA"),
        entry(Pattern.compile("HORTICOLA"), "HORTÍCOLA"),
        entry(Pattern.compile("CITRICOLA"), "CITRÍCOLA"),
        entry(Pattern.compile("FLORICOLA"), "FLORÍCOLA"),
        entry(Pattern.compile("FRUTICOLA"), "FRUTÍCOLA"),
        entry(Pattern.compile("PISCICOLA"), "PISCÍCOLA"),
        entry(Pattern.compile("\\bAPICOLA"), "APÍCOLA"),
        entry(Pattern.compile("FRUCTICOLA"), "FRUCTÍCOLA"),
        entry(Pattern.compile("OLERICOLA"), "OLERÍCOLA")
    );

    private final Pattern patronLogia = Pattern.compile("(LOGIA)");

    Map<Pattern, String> sustitucionesLogia = Map.<Pattern, String>ofEntries(
        entry(Pattern.compile("\\bKINESIOLOGIA"), "KINESIOLOGÍA"),
        entry(Pattern.compile("\\bGINECOLOGIA"), "GINECOLOGÍA"),
        entry(Pattern.compile("\\bODONTOLOGIA"), "ODONTOLOGÍA"),
        entry(Pattern.compile("\\bGASTROENTEROLOGIA"), "GASTROENTEROLOGÍA"),
        entry(Pattern.compile("PATOLOGIA\\b"), "PATOLOGÍA"),
        entry(Pattern.compile("CITOLOGIA\\b"), "CITOLOGÍA"),
        entry(Pattern.compile("FISIOLOGIA\\b"), "FISIOLOGÍA"),
        entry(Pattern.compile("\\bCANCEROLOGIA\\b"), "CANCEROLOGÍA"),
        entry(Pattern.compile("\\bTRAUMATOLOGIA\\b"), "TRAUMATOLOGÍA"),
        entry(Pattern.compile("\\bFONOAUDIOLOGIA\\b"), "FONOAUDIOLOGÍA"),
        entry(Pattern.compile("\\bNEFROLOGIA\\b"), "NEFROLOGÍA"),
        entry(Pattern.compile("\\bNEUROLOGIA\\b"), "NEUROLOGÍA"),
        entry(Pattern.compile("\\bINFECTOLOGIA\\b"), "INFECTOLOGÍA"),
        entry(Pattern.compile("\\bRADIOLOGIA\\b"), "RADIOLOGÍA"),
        entry(Pattern.compile("\\bHERPETOLOGIA\\b"), "HERPETOLOGÍA"),
        entry(Pattern.compile("\\bANESTESIOLOGIA\\b"), "ANESTESIOLOGÍA"),
        entry(Pattern.compile("LARINGOLOGIA\\b"), "LARINGOLOGIA"),
        entry(Pattern.compile("\\bPARASITOLOGIA\\b"), "PARASITOLOGÍA"),
        entry(Pattern.compile("\\bDIABETOLOGIA\\b"), "DIABETOLOGÍA"),
        entry(Pattern.compile("PSICOLOGIA\\b"), "PSICOLOGÍA"),
        entry(Pattern.compile("\\bMETODOLOGIA"), "METODOLOGÍA"),
        entry(Pattern.compile("\\bANTROPOLOGÍA\\b"), "ANTROPOLOGÍA"),
        entry(Pattern.compile("\\bDIABETOLOGIA\\b"), "DIABETOLOGÍA"),
        entry(Pattern.compile("\\bMETROLOGIA\\b"), "METROLOGÍA"),
        entry(Pattern.compile("\\bCARDIOLOGIA\\b"), "CARDIOLOGÍA"),
        entry(Pattern.compile("\\bOFTALMOLOGIA\\b"), "OFTALMOLOGÍA"),
        entry(Pattern.compile("\\bDERMATOLOGIA\\b"), "DERMATOLOGÍA"),
        entry(Pattern.compile("\\bECOLOGIA\\b"), "ECOLOGÍA"),
        entry(Pattern.compile("\\bMETEOROLOGIA\\b"), "METEOROLOGÍA"),
        entry(Pattern.compile("\\bHIDROLOGIA\\b"), "HIDROLOGÍA"),
        entry(Pattern.compile("\\bRADIOLOGIA"), "RADIOLOGÍA"),
        entry(Pattern.compile("\\bHEMATOLOGIA\\b"), "HEMATOLOGÍA"),
        entry(Pattern.compile("\\bCOLOPROCTOLOGIA\\b"), "COLOPROCTOLOGÍA"),
        entry(Pattern.compile("\\bANTROPOLOGIA\\b"), "ANTROPOLOGÍA"),
        entry(Pattern.compile("\\bTEOLOGIA\\b"), "TEOLOGÍA"),
        entry(Pattern.compile("\\bFLEBOLOGIA\\b"), "FLEBOLOGÍA"),
        entry(Pattern.compile("\\bLINFOLOGIA\\b"), "LINFOLOGÍA"),
        entry(Pattern.compile("\\bOTORRINOLARINGOLOGIA\\b"), "OTORRINOLARINGOLOGÍA"),
        entry(Pattern.compile("\\bNEUMOLOGIA\\b"), "NEUMOLOGÍA"),
        entry(Pattern.compile("\\bSOCIOLOGIA\\b"), "SOCIOLOGÍA"),
        entry(Pattern.compile("\\bPOLITOLOGIA\\b"), "POLITOLOGÍA"),
        entry(Pattern.compile("\\bINMUNOLOGIA\\b"), "INMUNOLOGÍA"),
        entry(Pattern.compile("\\bONCOLOGIA\\b"), "ONCOLOGÍA"),
        entry(Pattern.compile("\\bGEOLOGIA\\b"), "GEOLOGÍA"),
        entry(Pattern.compile("\\bTOXICOLOGIA\\b"), "TOXICOLOGÍA"),
        entry(Pattern.compile("\\bPERINATOLOGIA\\b"), "PERINATOLOGÍA"),
        entry(Pattern.compile("\\bREUMATOLOGIA\\b"), "REUMATOLOGÍA"),
        entry(Pattern.compile("\\bINMONOLOGIA\\b"), "INMONOLOGÍA"),
        entry(Pattern.compile("\\bMASTOZOOLOGIA\\b"), "MASTOZOOLOGÍA"),
        entry(Pattern.compile("\\bMICROBIOLOGIA\\b"), "MICROBIOLOGÍA"),
        entry(Pattern.compile("\\bANTROPOLOGIA\\b"), "ANTROPOLOGÍA"),
        entry(Pattern.compile("\\bGERONTOLOGIA\\b"), "GERONTOLOGÍA"),
        entry(Pattern.compile("\\bGERIATRIA\\b"), "GERIATRÍA"),
        entry(Pattern.compile("\\bRADIOLOGIA\\b"), "RADIOLOGÍA"),
        entry(Pattern.compile("\\bIMAGENOLOGIA\\b"), "IMAGENOLOGÍA"),
        entry(Pattern.compile("\\bENDOCRINOLOGIA\\b"), "ENDOCRINOLOGÍA"),
        entry(Pattern.compile("BIOLOGÍA\\b"), "BIOLOGÍA")


    );

    private final Pattern formatoAceptable = Pattern.compile("^(?!\\p{Lu}\\p{Ll}+, \\p{Lu}\\p{Ll}+$).*");
    private final Pattern noDigito = Pattern.compile("\\D+");

    public ObtenerContribuyentesDesdeZipTask(String archivoZip) {
        this.archivoZip = archivoZip;
    }

    final int CAMPOS_POR_REGISTRO = 5;

    enum Campo {
        RUC(0),
        DENOMINACION(1),
        DV(2),
        RUC_ANTERIOR(3),
        STATUS(4);

        public final int pos;

        Campo(int posicion) {
            this.pos = posicion;
        }

    }

    @Override
    protected List<Contribuyente> call() {

        File f = new File(archivoZip);

        if (!f.exists()) {
            return Collections.emptyList();
        }

        String fileName = f.getName();

        updateProgress(0, 100);
        updateTitle(fileName);
        updateMessage("Procesando basura de la SET... " + fileName);


        List<Contribuyente> lista = obtenerListaDesdeZip(f);

        updateProgress(100, 100);
        updateMessage("Finalizado " + fileName);
        updateTitle(fileName + " procesado");


        return lista;
    }

    private List<Contribuyente> obtenerListaDesdeZip(File f) {

        List<Contribuyente> colectado = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(f)) {

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                if (!zipEntry.getName().matches("ruc[0-9|X]\\.txt")) {
                    System.err.println("Archivo " + zipFile.getName() + " contiene entradas con nombres no aceptados");
                    continue;
                }

                colectado.addAll(parseEntry(zipFile, zipEntry));

            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return colectado;
    }


    private Collection<? extends Contribuyente> parseEntry(ZipFile zipFile, ZipEntry entry) {
        List<Contribuyente> parsed = new ArrayList<>();

        var lineaConverter = lineaConverter();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8))) {
            long cnt = 0;
            // String linea ; while((linea = bufferedReader.readLine()) != null){
            for (String linea; (linea = bufferedReader.readLine()) != null; ) {
                cnt++;
                if ((cnt % 5000) == 0) {
                    updateMessage(String.format("Registro %d ...", cnt));
                }

                String[] campos = linea.split("\\|");

                if (campos.length != CAMPOS_POR_REGISTRO) {
                    System.err.println(MessageFormat.format("Registro defectuoso con {0} campos en {1}\nRegistro: {2}", campos.length, entry.getName(), cnt));
                    System.err.println(Arrays.toString(campos));
                    System.err.println();
                    continue;
                }

                if (campos[STATUS.pos].equalsIgnoreCase("bloqueado")) {
                    //System.err.println("RUC bloqueado");
                    continue;
                }

                if (campos[STATUS.pos].equalsIgnoreCase("cancelado")) {
                    //System.err.println("RUC cancelado");
                    continue;
                }


                parsed.add(lineaConverter.apply(campos));
            }


            return parsed;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    private Function<String[], Contribuyente> lineaConverter() {
        return campos -> {
            if (campos != null && campos.length == CAMPOS_POR_REGISTRO) {
                String ruc = campos[RUC.pos];
                String denominacion = campos[DENOMINACION.pos];
                String denominacionCorregida = corregir(denominacion);
                int dv = Integer.parseInt(campos[DV.pos]);
                String rucAnterior = campos[RUC_ANTERIOR.pos];
                String estado = campos[STATUS.pos];
                boolean activo = campos[STATUS.pos].equalsIgnoreCase("activo");

                String notas = analizar(campos);
                return new Contribuyente(ruc, denominacion, denominacionCorregida, dv, rucAnterior, estado, activo, notas);
            } else {
                return null;
            }
        };
    }


    private String corregir(String denominacion) {

        String copia = denominacion.trim().replaceAll("\"", "")
            .replaceAll("^(\\.,\\s*)(.*)$", "$2")
            .replaceAll("^(\\s*,\\s*)(.*)$", "$2")
            .replaceAll("''", "")
            .replaceAll("¨", "")
            .replaceAll("´", "")
            .replaceAll("`", "")
            .replaceAll("\\.-", "")
            .replaceAll("`", "")
            .replaceAll("\\s{2,}", " ")
            .replaceAll(" \\., ", ", ")
            .replaceAll(" ,", ", ");


        for (String s : sustitucionesDirectas.keySet()) {
            if (copia.contains(s)) {
                copia = copia.replace(s, sustitucionesDirectas.get(s));
            }
        }


        for (Pattern p0 : sustitucionesPersJurid.keySet()) {
            copia = p0.matcher(copia).replaceAll(sustitucionesPersJurid.get(p0));
        }

        for (Pattern p1 : sustitucionesRegex.keySet()) {
            copia = p1.matcher(copia).replaceAll(sustitucionesRegex.get(p1));
        }

        for (Pattern p2 : sustitucionesNombresRegex.keySet()) {
            copia = p2.matcher(copia).replaceAll(sustitucionesNombresRegex.get(p2));
        }

        if (patronIcola.matcher(copia).find()) {
            for (Pattern p3 : sustitucionesIcola.keySet()) {
                copia = p3.matcher(copia).replaceAll(sustitucionesIcola.get(p3));
            }
        }

        if (patronLogia.matcher(copia).find()) {
            if (!copia.matches("EULOGIA")) {
                for (Pattern p4 : sustitucionesLogia.keySet()) {
                    copia = p4.matcher(copia).replaceAll(sustitucionesLogia.get(p4));
                }
            }
        }

        if(patronHttps.matcher(copia).find()) {
            copia = patronHttpsSustitucion.matcher(copia).replaceAll(",");
        }

        if (!copia.equals(denominacion)) {
            return copia.trim();
        }

        return null;
    }

    private String analizar(String[] r) {

        final StringBuilder sb = new StringBuilder();

        String ruc = r[RUC.pos];
        String denominacion = r[DENOMINACION.pos];
        int dv = Integer.parseInt(r[DV.pos]);
        int esperado = DigitoVerificador.para(ruc);

        if (denominacion.contains("?") || denominacion.contains("¿")) {
            sb.append("Nombre con problema de encoding");
        }

        Matcher m = formatoAceptable.matcher(denominacion);
        if (!m.matches()) {
            sb.append("El nombre tiene formato defectuoso. ");
        }

        m = noDigito.matcher(ruc);
        if (m.find()) {
            sb.append("El RUC contiene caracteres no numéricos. ");
        }

        if (dv != esperado) {
            sb.append(String.format("DV %d incorrecto, debe ser %d. ", dv, esperado));
        }

        return sb.toString();
    }

//    private Collection<? extends Contribuyente> parseEntry(ZipFile zipFile, ZipEntry entry) {
//        List<Contribuyente> parsed = new ArrayList<>();
//
//        var recordConverter = recordConverter();
//        try (
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8));
//            CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.newFormat('|'))
//        ) {
//            for (CSVRecord csvRecord : csvParser) {
//                if ((csvRecord.getRecordNumber() % 1000) == 0) {
//                    updateMessage(String.format("Registro %d ...", csvRecord.getRecordNumber()));
//                }
//
//                if (csvRecord.size() != CAMPOS_POR_REGISTRO) {
//                    System.err.println("Registro defectuoso con " + csvRecord.size() + " campos en " + entry.getName() + "\nRegistro: " + csvRecord + "\n");
//                    continue;
//                }
//
//                if (csvRecord.get(STATUS.pos).equalsIgnoreCase("bloqueado")) {
//                    //System.err.println("RUC bloqueado");
//                    continue;
//                }
//
//                if (csvRecord.get(STATUS.pos).equalsIgnoreCase("cancelado")) {
//                    //System.err.println("RUC cancelado");
//                    continue;
//                }
//
//
//                parsed.add(recordConverter.apply(csvRecord));
//
//
//            }
//
//            return parsed;
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }


//    private String analizar(CSVRecord r) {
//
//        final StringBuilder sb = new StringBuilder();
//
//        String ruc = r.get(RUC.pos);
//        String denominacion = r.get(DENOMINACION.pos);
//        int dv = Integer.parseInt(r.get(DV.pos));
//        int esperado = DigitoVerificador.para(ruc);
//
//        if (denominacion.contains("?") || denominacion.contains("¿")) {
//            sb.append("Nombre con problema de encoding");
//        }
//
//        Matcher m = formatoAceptable.matcher(denominacion);
//        if (!m.matches()) {
//            sb.append("El nombre tiene formato defectuoso. ");
//        }
//
//        m = noDigito.matcher(ruc);
//        if (m.find()) {
//            sb.append("El RUC contiene caracteres no numéricos. ");
//        }
//
//        if (dv != esperado) {
//            sb.append(String.format("DV %d incorrecto, debe ser %d. ", dv, esperado));
//        }
//
//        return sb.toString();
//    }

//    private Function<CSVRecord, Contribuyente> recordConverter() {
//        return r -> {
//            String ruc = r.get(RUC.pos);
//            String denominacion = r.get(DENOMINACION.pos);
//            String denominacionCorregida = corregir(denominacion);
//            int dv = Integer.parseInt(r.get(DV.pos));
//            String rucAnterior = r.get(RUC_ANTERIOR.pos);
//            String estado = r.get(STATUS.pos);
//            boolean activo = r.get(STATUS.pos).equalsIgnoreCase("activo");
//
//            String notas = analizar(r);
//            return new Contribuyente(ruc, denominacion, denominacionCorregida, dv, rucAnterior, estado, activo, notas);
//        };
//    }

}
