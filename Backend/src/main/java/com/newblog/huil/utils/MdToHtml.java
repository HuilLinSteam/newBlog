package com.newblog.huil.utils;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
/**
 * @author HuilLIN
 */
public class MdToHtml {
    public static String convert(String md) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        // You can re-use parser and renderer instances
        Node document = parser.parse(md);
        // "<p>This is <em>Sparta</em></p>\n"
        String html = renderer.render(document);
        return html;
    }
}
