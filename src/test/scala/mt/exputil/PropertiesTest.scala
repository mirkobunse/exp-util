package mt.exputil

import org.scalatest._
import java.io.FileNotFoundException

class PropertiesTest extends FlatSpec with Matchers {

  val path    = "src/test/resources/test.properties"
  val outpath = "target/testout.properties"
  val name    = "PropertiesTest"
  val p = Properties.read(path, name)

  "Properties" should "read specified properties" in {
    p apply "prop1" shouldBe "abc"
    p apply "property" shouldBe "4"
  }

  it should "add runtime properties" in {
    p apply Properties.EXPERIMENT_NAME shouldBe name
    p apply Properties.BASE_PROPERTIES shouldBe path
    p apply Properties.START_TIME
  }

  it should "fail for missing properties" in {
    a[NoSuchElementException] shouldBe thrownBy(p apply "missing")
  }

  it should "fail for missing file" in {
    a[FileNotFoundException] shouldBe thrownBy(
      Properties.read("src/test/resources/missing.properties", name))
  }

  Properties.write(outpath, p) // should check output manually

}