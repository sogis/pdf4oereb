package ch.so.agi.oereb.pdf4oereb.saxon.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.om.NoNamespaceName;
import net.sf.saxon.om.NodeName;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.tree.util.Orphan;
import net.sf.saxon.type.Type;

public class FixImageTest {
    Logger log = LoggerFactory.getLogger(FixImageTest.class);

    @Test
    // Apache FOP seems to have problems with 8bit (paletted images).
    public void fixEmbeddedSymbol_Ok() throws SaxonApiException {
		Processor processor = new Processor(false);

        String badSymbolString = "iVBORw0KGgoAAAANSUhEUgAAAEgAAAAkCAMAAAAdBYxUAAAB9VBMVEX8RiJWFwowDQZWFwtjGw2VKRSGJRJbGAuPJxPMORszDQbwQiDnQB/wQiFrHQ7IOBvTOhw2DgfYPB3rQh+AIxDkPx4vDAb4RSHbPR5PFQqcKxTZPB0SBQJ3IQ9yHw/BNRoGAQBAEgjDNhptHg4FAQDxQyEzDgaxMRgyDQY3DgcWBgMHAgBRFQpsHQ7FNhrtQh8EAQBBEggyDAVtHQ4pCwVEEggJAgHzQyEVBgIsCwQYBgMqCwS7MxiYKhTePR5NFAg1DgVQFQryQyEoCgQvDAXqQB/UOhwTBQI4DwdJFAhCEQdVFwoHAgFTFwo3DgVLFAjfPR4MAwEsCwSlLhbdPR5OFQgqCwQRBAJoHA4fCAREEwhXFwtJFAk8EAczDgUnCgVIEwi8NRlUFwqZKhQsDAZvHw5iGw17Ig82DgZmHA0IAgENAwEqCwUKAgFQFQoQBAIrCwRnHA1PFQgcCAPdPR1CEghWFgpIFAk5DwcyDAUlCgVIEgifKxUUBQLCNhrlPx+KJhKCIxCRKBMzDgbINxswDAW/NRgUBQKwMRcXBgIZBwMrCwRRFgoLAwEsDAUPBAIHAQBlHA03Dwd8IhBgGgtvHg4xDQabKxRSFgrANRpGEggpCwU0DgU9EAdLFAlaGAtFEwglCQRpHA0QBAIBAAADAAAAAADlALDwAAAApHRSTlPNxfTs6d/i6+DV88/Rz+fW1PPT0OPR9M7T7d7T++Xm1/7w1+f+z6Pa9PL6/sjn1tD+8Nvn9s/9z/rd+d/Y39LK2MjP4NzQ1PvyzNDG/sfXy9L83tzTyuD76Pjw6+7x2fbN2Oze9ebp5PLo/fz1/e373+jJ+NPw7O/y2vfN3frX0eHj4PPX9Nj72vr53u399fz+6fLk6uf03u3XzvXZ8e7r8Pbo/IbrDpEAAAK0SURBVEiJrdbnV1MxFADwpIhKtbVaJ69qJ8WWDilgURCUDQIy3Xvvvffee+9V+neam5u8QZ4fPH3vQ/NyT97vNMm9OSFVlD+NLdgOtmNLh8ew7akVgSensb1ySQRWrcG2/iv7qSKUwLNkMW/Iwvnz8MU7F9s5rpn4MmUqtj6XH1+mTce2wj2D/VKESnYEVLqDkAMOqWeQE05ZK6GOOOWUNDrisIm1THYONG/Z0ZWN/jn28UNGOunInUSwkHdHwv9w5PYbzu/NXUX9aUBHO2eElmu2joR0Z/+movnhTlPAHEqm7BwB6c6pMzD28MaR56OdzRcmivz/cGdopKPj4UV4C2g2DkLG+hyEkYkKsc6+EKxPCGIbcJ2f8tnZOBwynH1JNm5il2W/wvBt9iwGzg9BL606s8YJNe37Gxi21brvOYi9kvvuhV5EdWbXkEFT/nTDsJvW/KmGWFw4JA69o6rjoaTdlIcrYVidNQ/zEFsvHLIaelHV0fMI83kdDBu1OCQKsbXCITHoBVVHh7AuOOSxOL6CgDAPOZRUHQmJ+uJT0yyO6wFOTeQzn1pCdQQk65Qv7FWL43/PF1vWxW3ouVUHIb3eH8Ow79Z6T/ENkPX1iG+/6izoZZBxbtwNQkJ+spwbPCGPZNC5dw16YdVZNECo+fw5BOOevRNO3TdoeIns5o77OJaI6lRSMmw+x2J8lRq81/2ezlsvWNGyR3sJsW1x//1f3Vi0Ng6b2JjJIeTG60nHCNv3lOUYCTTZOXoe6fX19qfpMzjY2H5pISMUsv0/OmSu01h52eVAtnBi7489GZmH6dyXaDCYr87ZrXOlvv3/ec7bOQg54JA+BjnhLG0j1BFnWT/pccRZQUmtI45xsJXoMAavfjvlBe+kuM993i4veDXigtcqAuMi0DuAbV9bP1z9/gJpFg+vX350qQAAAABJRU5ErkJggg==";

        XQueryCompiler q = processor.newXQueryCompiler();
		XQueryEvaluator e = q.compile("<FubarLogo>"+badSymbolString+"</FubarLogo>").load();
		XdmNode badSymbolStringNode = (XdmNode)e.evaluateSingle();
		
		XdmNode[] arguments = {badSymbolStringNode};
		FixImage fixImage = new FixImage();
        XdmAtomicValue resultSymbol = (XdmAtomicValue) fixImage.call(arguments);
        
        String expectedResult = "iVBORw0KGgoAAAANSUhEUgAAAEgAAAAkCAIAAACluesxAAAEwklEQVR42t2ZTWwbRRTH/RXbTZo6cWI7jpM0jb2JP/LhbJISx038Edsb1alcShIQARda0kLVSFVbGqUhEhcOcEAcOHCBAxKolXrhDFeQgKpSJU5wAFQ4ABJUChw4IPH3B9bszOx6u+YAa40UZXaeZn47b/7vvR3T/tZzr6xmqbYyLlzNJ9n+K7mF1Ykxth9NioWuFTgm28uJ0mSYa1KIha5LJ9j+y+n501MRrkk+Grwhpdj+i6njZ8Ro419AmfDnr0unyPbq3Nhr82NUJ9r35fy8t/uncxL7aFcU3kxG2f5vy7mEr/sXnsmVqZG3lybY/q83szD5bWuFffRidOjdTJzt/+rpzGKf++DCyUYPoGgwA1BxwIxBRYMZhgoNR64OZiSq8+HBtZlYBcxgVO/npiuuCGU3GFX9jCFePSrV64nwB/npFyJDUz1HfIfsbRaz3WLucbQdtlnLo4FbBfGhfKEk1b2N1N6MsOh3BzqcDovZbDL1dziwUHTe3Vj6V6j4cq9OtRMPlo75AGNS/XXYrCzVd+XcqWGfuuHqsA/DWqTig6lQYYmCq8Ok7UdRfXom6W32Omo/p9V8UxRaoeKAqVC9HA+OuNrJFbTbrDfioY+Kx2e9rgfl/DebWfjnE8E+6z9g5F5RVC67DYa/nl9B+7iUWA70kk8xGCa6qWgw9XOV7u8h58YJwWRctYAeFId95Lkqyj3Q47SzavHsaIDySd1UMjB1qv25UXhIY1bsyWdrJzRqICSBXDGU5oF8N+pLHBvANpIjITP6qO4/lc5FghWwpsr+RiJCTrke8mtX9j1RIG2vxkeUlH1XPhI6qY8q5e/ZkVImVCJN41VWfgbeSU9qj1dJXzdp+0lpQSle4RE5UvS49FEdXChWXBH1VdMoPHj4EDnldO8R7VEYYYq0/fFsQSkK/3C2QI5EbNRHpRjH2NzCabWQU0IDtecWWB9p+3t1Ym4UxiNyJMK3PipFMDZjosAOiMU1zZjM8jDVAGOjMAUGudJHxQfj5oGUK1JBRj0P7JNHsJorcnMLyhURUfRRccCUsltKAO5Is9qz2wVGPJQypveycXIklEMfFQ2mkrMPdcp27MmQX3vOflMu4hAepTxwQC4zkHt9VH++tLq1OFcHU69E7m+kHBZZgP5ifUljJUIF6C677SGTdoAq4e3yt8vAasm+DqrN0cDG7HgFTEt9dT0eJGftbLPuzwhcKuzJyqCH7KRSqufDA2we+AwvpdJHdbsgVlxRioW0VI2QLOqkoTbBG8F7xeZgDiTBbyWjbkeblcjuaw1i45SLvjTowWGD4edryQl3Z5aXBOumqp+xa4Wkxlr453NSRr6CpmVLQ9lx0jSWLRiGGqcVKsU4ppIHbk8cPX3M532UQrOhgdiEYrNCs1gtNFuk4oNpyW7hlpgY04+7O73VTwPws16nfdbbdTE69CHxaYBV9sqnAVGY9riQkTiqDTkXnHxPFFpRC5KKA/bf+RrTChUNZhiqSq1UzNTBjET15fpSNlwtNA1Glenv3T2ZNm0vJwxG9Uet0CxNhg1GpVZo/q+p6mDsVe3lzLzi5arCTeml9GOPEzel1OXqjsLl6tpMjGuSi/BNkLMju2X7oYFQC5wr8qr2b6g3sGlqZFJcAAAAAElFTkSuQmCC";
        
        assertEquals(expectedResult, resultSymbol.getStringValue(), "Fixed symbol is not equal.");        
    }
}