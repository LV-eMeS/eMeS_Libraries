package lv.emes.libraries.tools.xml;

import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_StringList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static lv.emes.libraries.file_system.MS_FileSystemTools.deleteFile;
import static lv.emes.libraries.utilities.MS_StringUtils.getTabSpace;
import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_XMLFileTest {

    private static final String PATH_TO_XML_FILE = TestData.TEMP_DIR + "MS_XMLFileTest.xml";
    private static final String PATH_TO_XML_FILE2 = TestData.TEMP_DIR + "MS_XMLFileTest2.xml";
    private static final String PATH_TO_XML_FILE3 = TestData.TEMP_DIR + "MS_XMLFileTest3.xml";
    private static MS_StringList FOOD_NAME_LIST = null;
    private static MS_XMLReader file1;
    private static MS_XMLReader file2;
    private static MS_XMLReader file3;

    @Test
    public void test01ParseSimpleXMLFile() {
        MS_XMLReader file = file1;
        assertThat(file.getRootElementName()).isEqualTo("breakfast_menu");
        //everything starting from first <food> tag and ending with very last </food> tag
        //<breakfast_menu><food>...</food><food>...</food></breakfast_menu>
        MS_XMLElementNodeList allFoods = file.getNodesByTagName("food");
        assertThat(allFoods.count()).isEqualTo(5);
        assertThat(allFoods.getTag()).isEqualTo("food");
        for (int i = 0; i < allFoods.count(); i++) {
            //everything that begins with <food> and ends with </food> in
            //<food>...</food>
            MS_XMLElementNode aCurrentFood = allFoods.get(i);
            assertThat(aCurrentFood.getTagName()).isEqualTo("food");
            assertThat(aCurrentFood.getFirstChild("name").getValue()).isEqualTo(FOOD_NAME_LIST.get(i));
            assertThat(aCurrentFood.getFirstChild("name").getTagName()).isEqualTo("name");
            assertThat(aCurrentFood.getChildList("name").count()).isEqualTo(1);
            assertThat(aCurrentFood.getChildList("name").get(0).getValue()).isEqualTo(FOOD_NAME_LIST.get(i));
        }

        //just testing iterating
        allFoods.forEachItem((node, ind) -> {
            assertThat(node.getTagName()).isEqualTo("food"); //like assertEquals("food", aCurrentFood.getTagName());
            assertThat(node.getFirstChild("name").getTagName()).isEqualTo("name"); //like assertEquals("name", aCurrentFood.getFirstChild("name").getTagName());
            assertThat(node.getChildList("name").count()).isEqualTo(1);
            assertThat(node.getFirstChild("name").getValue()).isEqualTo(FOOD_NAME_LIST.get(ind));
        });

        assertThat(allFoods.get(1).getFirstChild("description").getValue()).isEqualTo("Light Belgian waffles covered with strawberries and whipped cream");
        assertThat(allFoods.get(3).getFirstChild("calories").getValue()).isEqualTo("600");
        assertThat(allFoods.get(4).getFirstChild("calories").getValue()).isEqualTo("950");
        try {
            allFoods.get(5).getFirstChild("calories").getValue();
        } catch (MS_XMLElementNodeList.NodeNotFoundException ex) {
            assertThat(true).isTrue();
        }
    }

    @Test
    public void test02DocumentObject() throws ParserConfigurationException, SAXException, IOException {
        MS_XMLReader file = new MS_XMLReader(PATH_TO_XML_FILE);
        Document doc = file.getDocument();
//        assertEquals("1.0", doc.getXmlVersion());
//        assertEquals("UTF-8", doc.getInputEncoding());
//        doc.setXmlVersion("1.1");
//        assertEquals("1.1", doc.getXmlVersion());
        assertThat(doc.getBaseURI()).isEqualTo("file:/" + PATH_TO_XML_FILE);
    }

    @Test
    public void test03WrongParsing() {
        MS_XMLReader file = file1;
        boolean exceptionCaught = false;
        try {
            file.getNodesByTagName("princesses");
        } catch (MS_XMLReader.NodesNotFoundException e) {
            exceptionCaught = true;
        }
        assertThat(exceptionCaught).isTrue();

        MS_XMLElementNodeList prices = file.getNodesByTagName("price");
        assertThat(prices.get(2).getFirstChild("child")).isNull();
    }

    @Test
    public void test04ParseAndEditElements() {
        MS_XMLReader file = file1;
        MS_XMLElementNodeList allFoods = file.getNodesByTagName("food");
        allFoods.forEachItem((item, ind) -> {
            MS_XMLElementNode nameOfFood = item.getFirstChild("name");
            nameOfFood.toElement().setTextContent(FOOD_NAME_LIST.get(allFoods.count() + ind));
            assertThat(nameOfFood.getValue()).isEqualTo(FOOD_NAME_LIST.get(allFoods.count() + ind));
        });

        //now to check for a new values
        allFoods.forEachItem((node, index) -> {
//            System.out.println(node);
            assertThat(node.getFirstChild("name").getValue()).isEqualTo(FOOD_NAME_LIST.get(index + 5));
        });
    }

    @Test
    public void test05ParseComplicatedXMLFile() {
        MS_XMLReader file = file2;
//        System.out.println(file.toString());
        assertThat(file.getRootElementName()).isEqualTo("Students");
        MS_XMLElementNodeList allTheStudents = file.getNodesByTagName("Student");
        MS_XMLElementNode studentMaris = allTheStudents.get(0);
        MS_XMLElementNode studentJanis = allTheStudents.get(1);
        assertThat(studentMaris.getFirstChild("name").getValue()).isEqualTo("Māris");
        assertThat(studentJanis.getFirstChild("name").getValue()).isEqualTo("Jānis");

        MS_XMLElementNode university = studentMaris.getFirstChild("university");
        assertThat(university.getAttribute("name")).isEqualTo("RTU");
        assertThat(university.getTagName()).isEqualTo("university");
        assertThat(university.getFirstChild("faculty").getValue()).isEqualTo("DITF");

        //Janis has no university in records because there is only 1 university registered
        assertThat(file.getNodesByTagName("university").size()).isEqualTo(1);
        assertThat(studentJanis.getFirstChild("university")).isNull();
    }

    @Test
    public void test06ParseXMLFileWithOnly1Tag() {
        assertThat(file3.getNodesByTagName(file3.getRootElementName()).get(0).getValue()).isEqualTo("");
    }

    @Test
    public void test07NodeIsNotAnElement() {
        MS_XMLElementNode textNode = file1.getRootNode().getAllChildNodes().get(0);
        assertThat(textNode.getTagName()).isEqualTo(MS_XMLElementNode._TEXT_NODE_NAME);
        assertThat(textNode.getAllChildNodes().length()).isEqualTo(0);
        assertThat(textNode.getFirstChild("")).isNull();
        assertThat(textNode.getChildList("")).isNull();
    }

    @Test
    public void test08ExportToJSON() {
        JSONObject jsonObject = file1.toJSON();
        assertThat(jsonObject.names().get(0)).isEqualTo("breakfast_menu");
        jsonObject = jsonObject.getJSONObject("breakfast_menu");
        assertThat(jsonObject.names().get(0)).isEqualTo("food");
        JSONArray jsonArray = jsonObject.getJSONArray("food");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            assertThat(json.get("name")).isEqualTo(FOOD_NAME_LIST.get(i));
        }
    }

    @BeforeClass
    public static void initTestPreConditions() throws IOException, ParserConfigurationException, SAXException {
        //create an XML file:
        //view-source:http://www.w3schools.com/xml/simple.xml
        MS_TextFile xmlFile = new MS_TextFile(PATH_TO_XML_FILE);
        xmlFile.writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<breakfast_menu>\n" +
                "\t<food>\n" +
                "\t\t<name>Belgian Waffles</name>\n" +
                "\t\t<price>$5.95</price>\n" +
                "\t\t<description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>\n" +
                "\t\t<calories>650</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>Strawberry Belgian Waffles</name>\n" +
                "\t\t<price>$7.95</price>\n" +
                "\t\t<description>Light Belgian waffles covered with strawberries and whipped cream</description>\n" +
                "\t\t<calories>900</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>Berry-Berry Belgian Waffles</name>\n" +
                "\t\t<price>$8.95</price>\n" +
                "\t\t<description>Light Belgian waffles covered with an assortment of fresh berries and whipped cream</description>\n" +
                "\t\t<calories>900</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>French Toast</name>\n" +
                "\t\t<price>$4.50</price>\n" +
                "\t\t<description>Thick slices made from our homemade sourdough bread</description>\n" +
                "\t\t<calories>600</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>Homestyle Breakfast</name>\n" +
                "\t\t<price>$6.95</price>\n" +
                "\t\t<description>Two eggs, bacon or sausage, toast, and our ever-popular hash browns</description>\n" +
                "\t\t<calories>950</calories>\n" +
                "\t</food>\n" +
                "</breakfast_menu>\n");
        xmlFile.close();

        FOOD_NAME_LIST = new MS_StringList();
        FOOD_NAME_LIST.add("Belgian Waffles");
        FOOD_NAME_LIST.add("Strawberry Belgian Waffles");
        FOOD_NAME_LIST.add("Berry-Berry Belgian Waffles");
        FOOD_NAME_LIST.add("French Toast");
        FOOD_NAME_LIST.add("Homestyle Breakfast");

        //new names
        FOOD_NAME_LIST.add("Cookies");
        FOOD_NAME_LIST.add("Cakes");
        FOOD_NAME_LIST.add("Peanuts");
        FOOD_NAME_LIST.add("Bananas");
        FOOD_NAME_LIST.add("Salads");

        xmlFile = new MS_TextFile(PATH_TO_XML_FILE2);
        xmlFile.writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlFile.writeln("<Students>");
        xmlFile.writeln(getTabSpace(1) + "<Student>");
        xmlFile.writeln(getTabSpace(2) + "<name>Māris</name>");
        xmlFile.writeln(getTabSpace(2) + "<age>24</age>");
        xmlFile.writeln(getTabSpace(2) + "<university name=\"RTU\">");
        xmlFile.writeln(getTabSpace(3) + "<faculty>DITF</faculty>");
        xmlFile.writeln(getTabSpace(2) + "</university>");
//        xmlFile.writeln(getTabSpace(3) + "<>");
        xmlFile.writeln(getTabSpace(1) + "</Student>");
        xmlFile.writeln(getTabSpace(1) + "<Student>");
        xmlFile.writeln(getTabSpace(2) + "<name>Jānis</name>");
        xmlFile.writeln(getTabSpace(2) + "<age>26</age>");
        xmlFile.writeln(getTabSpace(1) + "</Student>");
        xmlFile.writeln("</Students>");
        xmlFile.close();

        xmlFile = new MS_TextFile(PATH_TO_XML_FILE3);
        xmlFile.writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlFile.writeln("<Universities></Universities>");
        xmlFile.close();

        file1 = new MS_XMLReader(MS_BinaryTools.readFile(PATH_TO_XML_FILE));
        file2 = new MS_XMLReader(MS_BinaryTools.readFile(PATH_TO_XML_FILE2));
        file3 = new MS_XMLReader(MS_BinaryTools.readFile(PATH_TO_XML_FILE3));
    }

    @AfterClass
    public static void finalizeTestConditions() {
        deleteFile(PATH_TO_XML_FILE);
        deleteFile(PATH_TO_XML_FILE2);
        deleteFile(PATH_TO_XML_FILE3);
    }
}