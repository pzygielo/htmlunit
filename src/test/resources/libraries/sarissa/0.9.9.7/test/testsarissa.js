/**
 * ====================================================================
 * About
 * ====================================================================
 * Sarissa cross browser XML library - unit tests
 * @version @sarissa.version@
 * @author: Manos Batsis, mailto: mbatsis at users full stop sourceforge full stop net
 *
 * This module contains unit tests for Sarissa that run using EcmaUnit by Guido Wesdorp and
 * Philipp von Weitershausen, see http http://kupu.oscom.org/download/ecmaunit-0.3.html
 * Thanks for the great work guys!
 *
 * ====================================================================
 * Licence
 * ====================================================================
 * Sarissa is free software distributed under the GNU GPL version 2 (see <a href="gpl.txt">gpl.txt</a>) or higher, 
 * GNU LGPL version 2.1 (see <a href="lgpl.txt">lgpl.txt</a>) or higher and Apache Software License 2.0 or higher 
 * (see <a href="asl.txt">asl.txt</a>). This means you can choose one of the three and use that if you like. If 
 * you make modifications under the ASL, i would appreciate it if you submitted those.
 * In case your copy of Sarissa does not include the license texts, you may find
 * them online in various formats at <a href="http://www.gnu.org">http://www.gnu.org</a> and 
 * <a href="http://www.apache.org">http://www.apache.org</a>.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY,FITNESS FOR A PARTICULAR PURPOSE 
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/** @constructor */
function SarissaTestCase() {
    /** @final */
    this.name = 'SarissaTestCase';
    
    this.testGetEmptyDomDocument0 = function(){
        this.assert(Sarissa.getDomDocument());
    };
    
    this.testGetEmptyDomDocument1 = function(){
        this.assert(Sarissa.getDomDocument("", ""));
    };
    
    this.testGetEmptyDomDocument2 = function(){
        this.assert(Sarissa.getDomDocument(null, null));
    };
    
    this.testGetEmptyDomDocument3 = function(){
        this.assert(Sarissa.getDomDocument("", null));
    };
    
    this.testGetEmptyDomDocument4 = function(){
        this.assert(Sarissa.getDomDocument(null, ""));
    };
    
    this.testDomDocumentWithRoot = function(){
        var oDom = Sarissa.getDomDocument(null, "test");
        this.assertEquals(oDom.documentElement.nodeName, "test");
    };
    this.testDomDocumentWithRootNS = function(){
        var oDom = Sarissa.getDomDocument("http://geekologue.com/testPrefixPreservationDomDocument", "test");
        this.assert(oDom.documentElement);
    };
    this.testPrefixPreservationDomDocument = function(){
        var oDom = Sarissa.getDomDocument("http://geekologue.com/testPrefixPreservationDomDocument", "someprefix:test");
        this.assertEquals(oDom.documentElement.tagName, "someprefix:test");
    };
    this.testGetDomDocument = function(){
        this.assert(Sarissa.getDomDocument("http://foo.bar/","foo", null));
    };
    this.testStripTags = function(){
        this.assertEquals(Sarissa.stripTags("<root>this<s> could</s> be <a>wron</a>g</root>"), "this could be wrong");
    };
    this.testGetParseErrorTextNoError = function(){
        var oDom = Sarissa.getDomDocument(null,"foo", null);
        this.assertEquals(Sarissa.getParseErrorText(oDom), Sarissa.PARSED_OK);
    };
    this.testGetParseErrorTextError = function(){
        var oDom = Sarissa.getDomDocument();
        oDom.async = false;
        oDom.load("test-bad.xml");
        this.assert(Sarissa.getParseErrorText(oDom));
        document.getElementById("parseError").appendChild(document.createTextNode(Sarissa.getParseErrorText(oDom)));
        
    };
    
    /** Test the <code>Sarissa.copyChildNodes()</code> method */
    this.testCopyChildNodes = function(){
        var from = Sarissa.getDomDocument("","foo", null);
        from.documentElement.appendChild(from.createElement("elementName"));
        var to = Sarissa.getDomDocument("","bar", null);
        Sarissa.copyChildNodes(from, to);
        this.assertEquals(from.documentElement.tagName, to.documentElement.tagName);
    };
    
    /** Test the <code>Sarissa.moveChildNodes()</code> method */
    this.testMoveChildNodes = function(){
        var from = Sarissa.getDomDocument("","root", null);
        for(i=0;i<4;i++){
            from.documentElement.appendChild(from.createElement("elem"));
        };
        var to = Sarissa.getDomDocument("","bar", null);
        Sarissa.moveChildNodes(from.documentElement, to.documentElement);
        this.assertEquals(to.getElementsByTagName("elem").length, 4);
        this.assertEquals(from.getElementsByTagName("elem").length, 0);
    };
    
    /** Test the <code>Sarissa.clearChildNodes()</code> method */
    this.testClearChildNodes = function(){
        var from = Sarissa.getDomDocument("","foo", null);
        Sarissa.clearChildNodes(from);
        this.assertEquals(from.childNodes.length, 0);
    };

    /** Test the <code>Sarissa.getText()</code>*/ 
    this.testGetTextWithCdata = function(){
        var oDom = (new DOMParser()).parseFromString("<root xml:space='preserve'>This t<elem>ext has</elem> <![CDATA[ CDATA ]]>in<elem /> it</root>", "text/xml");
        this.assertEquals(Sarissa.getText(oDom.documentElement, true), "This text has  CDATA in it");
        this.assertEquals(Sarissa.getText(oDom, true), "This text has  CDATA in it");
    };

    /** Test the <code>Sarissa.getParseErrorText()</code> */
    this.testGetText = function(){
        var oDom = (new DOMParser()).parseFromString("<root xml:space='preserve'>This t<elem>ext has </elem>no CDATA in<elem /> it</root>", "text/xml");
        this.assertEquals(Sarissa.getText(oDom.documentElement, true), "This text has no CDATA in it");
        this.assertEquals(Sarissa.getText(oDom, true), "This text has no CDATA in it");
    };
    
    this.testXmlize = function(){
        var book = new Object();
        book.chapters = new Array();
        book.chapters[0] = "Kingdom of fools";
        book.chapters[1] = "Fall";
        book.chapters[2] = "Final battle";
        book.chapters[3] = "Characters that need to be escaped: << << \"' \"\"\"&&'' < > & ' \" ";
        book.chapters[4] = "Epilogue";
        book.editor = "Manos Batsis";
        var publisher = new Object();
        publisher.name = "Some Publisher";
        book.publisher = publisher;
        var s = Sarissa.xmlize(book, "book");
        document.getElementById("xmlization").appendChild(document.createTextNode(s));
    };
}
SarissaTestCase.prototype = new TestCase;

/** @constructor */
function XMLHttpRequestTestCase(){
    /** @final */
    this.name = 'XmlHttpRequestTestCase';

    /** Test the XMLHttpRequest constructor exists (natively or not) */
    this.test = function(){
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", "test.xml", false);
        xmlhttp.send('');
        // test for responseText to avoid MIME/local FS issues
        this.assertEquals(xmlhttp.responseText.indexOf("<root"), 0);
    };
};
XMLHttpRequestTestCase.prototype = new TestCase;

/** @constructor */
function XMLSerializerTestCase(){
    /** @final */
    this.name = 'XMLSerializerTestCase';

    /** Test the serializeToString method */
    this.testSerializeToString = function(){
        var serializer = new XMLSerializer();
        var oDoc = Sarissa.getDomDocument("","foo", null);
        // TODO: validate with a regexp 
        this.assert(serializer.serializeToString(oDoc));
    };
};
XMLSerializerTestCase.prototype = new TestCase;


/** @constructor */
function DOMParserTestCase(){
    /** @final */
    this.name = 'DOMParserTestCase';

    /** Test the serializeToString method */
    this.testParseFromString = function(){
        var parser = new DOMParser();
        var oDoc = parser.parseFromString("<root />", "text/xml");
        // TODO: validate with a regexp 
        this.assertEquals(oDoc.documentElement.tagName, "root");
    };
};
DOMParserTestCase.prototype = new TestCase;

/** Test the <code>XMLDocument.selectNodes()</code> method */
testSelectNodes = function() {
    this.xmlDoc = (new DOMParser()).parseFromString("<root/>", "text/xml");
    var nodeList = this.xmlDoc.selectNodes("*");
    this.assertEquals(nodeList.length, 1);
    this.assertEquals(nodeList.item(0), nodeList[0]);
};

/** Test the <code>XMLDocument.selectSingleNode()</code> method */
testSelectSingleNode = function() {
    this.xmlDoc = Sarissa.getDomDocument("", "root", null);
    var node = this.xmlDoc.selectSingleNode("*");
    this.assert(node);
    this.assertEquals(node.tagName, "root");
};

var isXmlDocumentAsyncLoadOK = false;
/** @constructor */
function XMLDocumentTestCase() {
    /** @final */
    this.name = 'XMLDocumentTestCase';
    this.xmlDoc = null;
    this.setUp = function() {
        this.xmlDoc = Sarissa.getDomDocument();
    };
    this.testLoad = function(){
        var oDom = Sarissa.getDomDocument("", "root", null);
        oDom.async = false;
        var oldDoc = oDom.load("test.xml");
        //this.assertEquals(oldDoc.getElementsByTagName("root").length, 1);
        this.assertEquals(oDom.getElementsByTagName("root").length, 1);
    };
    /** Test the <code>XMLDocument.selectNodes()</code> method */
    this.testSelectNodes = testSelectNodes
    /** Test the <code>XMLDocument.selectSingleNode()</code> method */
    this.testSelectSingleNode = testSelectSingleNode;
};
XMLDocumentTestCase.prototype = new TestCase;


/** @constructor */
function XMLElementTestCase() {
    /** @final */
    this.name = 'XMLElementTestCase';
    this.xmlDoc = null;
    this.setUp = function() {
        this.xmlDoc = Sarissa.getDomDocument();
    };
    /** Test the <code>XMLElement.selectNodes()</code> method */
    this.testSelectNodes = testSelectNodes
    /** Test the <code>XMLElement.selectSingleNode()</code> method */
    this.testSelectSingleNode = testSelectSingleNode;
};
XMLElementTestCase.prototype = new TestCase;

/** @constructor */
function XSLTProcessorTestCase() {
    /** @final */
    this.name = 'XSLTProcessorTestCase';
    var xmlDoc = Sarissa.getDomDocument();
    xmlDoc.async = false;
    xmlDoc = xslDoc = (new DOMParser()).parseFromString("<root><element attribute='value' /></root>", "text/xml");
    
    function transformToFragment(xslUrl, ownerDoc) {
        	var processor = new XSLTProcessor();
        	var xmlhttp = new XMLHttpRequest();
            xmlhttp.open("GET", xslUrl, false);
            xmlhttp.send('');
        	var xslDoc = xmlhttp.responseXML;
        	//alert(xslUrl+": \n"+new XMLSerializer().serializeToString(xslDoc));
        	processor.importStylesheet(xslDoc);
        	processor.setParameter("", "user", "hax");
        	return processor.transformToFragment(xmlDoc, ownerDoc);
    }
    
    function debug(node) {
        	var s = new XMLSerializer();
        	var console = document.getElementById('console');
        	console.innerHTML += '====\n' + s.serializeToString(node) + '\n\n';
    };
    
    this.testProcessor = function(){
        // create an instance of XSLTProcessor
        var processor = new XSLTProcessor();
        
        // create a DOM Document containing an XSLT stylesheet
        var xslDoc = Sarissa.getDomDocument();
        var xslStr = "<?xml version='1.0' encoding='UTF-8'?>"+
            "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform' >"+
                "<xsl:output method='xml' version='1.0' encoding='UTF-8' indent='yes'/>"+
                "<xsl:param name='title' />"+
                "<xsl:template match='/'><p class='test' title='{$title}'>test</p>"+
                "</xsl:template></xsl:stylesheet>";
        xslDoc = (new DOMParser()).parseFromString(xslStr, "text/xml");
        
        // make the stylesheet reusable by importing it in the 
        // XSLTProcessor
        processor.importStylesheet(xslDoc);
        var xmlDoc = Sarissa.getDomDocument(null,"foo", null);
        processor.setParameter("", "title", "test title");
        // transform the document 
        var newDocument = processor.transformToDocument(xmlDoc);
        // show transformation results
        this.assertEquals(newDocument.documentElement.tagName, "p");
        this.assertEquals(newDocument.documentElement.getAttribute("title"), "test title");
        this.assertEquals(processor.getParameter("", "title"), "test title");
    };
    
    this.testOutputXml1 = function () {
    	var f = transformToFragment('test-output-xml.xsl.xml', Sarissa.getDomDocument());
        this.assertEquals('#document-fragment', f.nodeName);
        this.assertEquals('div', f.firstChild.nodeName.toLowerCase());
    };
    this.testOutputXml2 = function () {
    	var f = transformToFragment('test-output-xml.xsl.xml', document);
        this.assertEquals('#document-fragment', f.nodeName);
        this.assertEquals('div', f.firstChild.nodeName.toLowerCase());
    };
    this.testOutputXHtml = function () {
    	var f = transformToFragment('test-output-html.xsl.xml', Sarissa.getDomDocument());
        this.assertEquals('#document-fragment', f.nodeName);
        this.assertEquals('div', f.firstChild.nodeName.toLowerCase());
    }
    this.testOutputHtml = function () {
    	var f = transformToFragment('test-output-html.xsl.xml', document);
        this.assertEquals('#document-fragment', f.nodeName);
        this.assertEquals('div', f.firstChild.nodeName.toLowerCase());
    }
    this.testOutputText = function () {
    	var f = transformToFragment('test-output-text.xsl.xml', Sarissa.getDomDocument());
        this.assertEquals('#document-fragment', f.nodeName);
        this.assertEquals('#text', f.firstChild.nodeName);
        this.assertEquals('some text', f.firstChild.nodeValue);
    }
    this.testFragment1 = function () {
    	var f = transformToFragment('test-xml-frag.xsl.xml', Sarissa.getDomDocument());
        this.assertEquals('#document-fragment', f.nodeName);

        this.assertEquals('p', f.firstChild.nodeName.toLowerCase());
        this.assertEquals('hr', f.lastChild.nodeName.toLowerCase());
    }
    this.testFragment2 = function () {
    	var f = transformToFragment('test-html-frag.xsl.xml', document);
        this.assertEquals('#document-fragment', f.nodeName);
        var children = f.querySelectorAll('*');
        this.assertEquals('p', children[0].nodeName.toLowerCase());
        this.assertEquals('hr', children[children.length - 1].nodeName.toLowerCase());
    }
}; 
XSLTProcessorTestCase.prototype = new TestCase;
