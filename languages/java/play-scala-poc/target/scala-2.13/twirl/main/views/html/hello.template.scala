
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
import play.api.mvc._
import play.api.data._

object hello extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template1[String,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(name: String):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](_display_(/*2.2*/main("Hello page title")/*2.26*/ {_display_(Seq[Any](format.raw/*2.28*/("""
    """),format.raw/*3.5*/("""<section id="top">
        <div class="wrapper">
            <h1>I love SBT+Play, and you """),_display_(/*5.43*/name),format.raw/*5.47*/("""</h1>
        </div>
    </section>
""")))}))
      }
    }
  }

  def render(name:String): play.twirl.api.HtmlFormat.Appendable = apply(name)

  def f:((String) => play.twirl.api.HtmlFormat.Appendable) = (name) => apply(name)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/hello.scala.html
                  HASH: 524ef912dc64244361a9950a473627562c2d049b
                  MATRIX: 729->1|837->17|869->41|908->43|939->48|1056->139|1080->143
                  LINES: 21->1|26->2|26->2|26->2|27->3|29->5|29->5
                  -- GENERATED --
              */
          