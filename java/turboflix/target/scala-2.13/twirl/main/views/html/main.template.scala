
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import java.lang._
import java.util._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.api.data.Field
import play.data._
import play.core.j.PlayFormsMagicForJava._
import scala.jdk.CollectionConverters._

object main extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template2[String,Html,play.twirl.api.HtmlFormat.Appendable] {

  /*
 * This template is called from the `index` template. This template
 * handles the rendering of the page header and body tags. It takes
 * two arguments, a `String` for the title of the page and an `Html`
 * object to insert into the body of the page.
 */
  def apply/*7.2*/(title: String)(content: Html):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*8.1*/("""
"""),format.raw/*9.1*/("""<!DOCTYPE html>
<html lang="en">
    <head>
        """),format.raw/*12.68*/("""
        """),format.raw/*13.9*/("""<title>"""),_display_(/*13.17*/title),format.raw/*13.22*/("""</title>
        """),format.raw/*14.46*/("""
        """),format.raw/*15.51*/("""
        """),format.raw/*16.9*/("""<link rel="stylesheet" media="screen" href=""""),_display_(/*16.54*/routes/*16.60*/.Assets.versioned("stylesheets/main.css")),format.raw/*16.101*/("""">
        <link rel="stylesheet" media="screen" href=""""),_display_(/*17.54*/routes/*17.60*/.Assets.versioned("stylesheets/bootstrap.min.css")),format.raw/*17.110*/("""">
        <link rel="stylesheet" media="screen" href=""""),_display_(/*18.54*/routes/*18.60*/.Assets.versioned("stylesheets/bootstrap-grid.min.css")),format.raw/*18.115*/("""">
        <link rel="stylesheet" media="screen" href=""""),_display_(/*19.54*/routes/*19.60*/.Assets.versioned("stylesheets/bootstrap-reboot.min.css")),format.raw/*19.117*/("""">
        <link rel="shortcut icon" type="image/png" href=""""),_display_(/*20.59*/routes/*20.65*/.Assets.versioned("images/favicon.png")),format.raw/*20.104*/("""">
    </head>
    <body>
        """),format.raw/*24.32*/("""
        """),_display_(/*25.10*/content),format.raw/*25.17*/("""

        """),format.raw/*27.9*/("""<script src=""""),_display_(/*27.23*/routes/*27.29*/.Assets.versioned("javascripts/custom.js")),format.raw/*27.71*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*28.23*/routes/*28.29*/.Assets.versioned("javascripts/jquery.min.js")),format.raw/*28.75*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*29.23*/routes/*29.29*/.Assets.versioned("javascripts/floatingUiCore.min.js")),format.raw/*29.83*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*30.23*/routes/*30.29*/.Assets.versioned("javascripts/floatingUiDom.min.js")),format.raw/*30.82*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*31.23*/routes/*31.29*/.Assets.versioned("javascripts/bootstrap.min.js")),format.raw/*31.78*/("""" type="text/javascript"></script>
    </body>
</html>
"""))
      }
    }
  }

  def render(title:String,content:Html): play.twirl.api.HtmlFormat.Appendable = apply(title)(content)

  def f:((String) => (Html) => play.twirl.api.HtmlFormat.Appendable) = (title) => (content) => apply(title)(content)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/main.scala.html
                  HASH: 8c7f45aba60b0816aee155d4f182f27754bb80dd
                  MATRIX: 1165->260|1289->291|1316->292|1396->403|1432->412|1467->420|1493->425|1538->479|1575->530|1611->539|1683->584|1698->590|1761->631|1844->687|1859->693|1931->743|2014->799|2029->805|2106->860|2189->916|2204->922|2283->979|2371->1040|2386->1046|2447->1085|2509->1209|2546->1219|2574->1226|2611->1236|2652->1250|2667->1256|2730->1298|2814->1355|2829->1361|2896->1407|2980->1464|2995->1470|3070->1524|3154->1581|3169->1587|3243->1640|3327->1697|3342->1703|3412->1752
                  LINES: 32->7|37->8|38->9|41->12|42->13|42->13|42->13|43->14|44->15|45->16|45->16|45->16|45->16|46->17|46->17|46->17|47->18|47->18|47->18|48->19|48->19|48->19|49->20|49->20|49->20|52->24|53->25|53->25|55->27|55->27|55->27|55->27|56->28|56->28|56->28|57->29|57->29|57->29|58->30|58->30|58->30|59->31|59->31|59->31
                  -- GENERATED --
              */
          