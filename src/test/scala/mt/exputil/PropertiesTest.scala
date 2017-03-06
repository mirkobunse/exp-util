package mt.exputil

import org.scalatest._
import java.io.FileNotFoundException
import mt.exputil.Properties._

class PropertiesTest extends FlatSpec with Matchers {

  val path    = "src/test/resources/test.properties"
  val outpath = "target/testout.properties"
  val name    = "PropertiesTest"
  val p       = Properties.read(path, name)

  "Properties" should "read specified properties" in {
    p apply "prop1"    shouldBe "abc"
    p apply "property" shouldBe "4"
    p apply "split1"   shouldBe "1,2,3,4,5"
    p apply "split2"   shouldBe "1,2,3,4,5"
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
  
  it should "implicitly split on 'split1' but not on 'split2'" in {
    p.splitOn("split1").zipWithIndex.foreach(f => {
      f._1 apply "prop1"    shouldBe "abc"
      f._1 apply "property" shouldBe "4"
      f._1 apply "split1"   shouldBe (f._2+1).toString()  // split
      f._1 apply "split2"   shouldBe "1,2,3,4,5"          // do not split
      f._1 apply Properties.EXPERIMENT_NAME shouldBe name
      f._1 apply Properties.BASE_PROPERTIES shouldBe path
      f._1 apply Properties.START_TIME
      a[NoSuchElementException] shouldBe thrownBy(f._1 apply "missing")
    })
  }
  
  it should "implicitly split on both 'split1' and 'split2'" in {
    p.splitOn("split1").splitOn("split2").foreach(f => {
      f apply "prop1"          shouldBe "abc"
      f apply "property"       shouldBe "4"
      f.apply("split1").length shouldBe 1  // split
      f.apply("split2").length shouldBe 1  // split, too
      f apply Properties.EXPERIMENT_NAME shouldBe name
      f apply Properties.BASE_PROPERTIES shouldBe path
      f apply Properties.START_TIME
      a[NoSuchElementException] shouldBe thrownBy(f apply "missing")
    })
  }

  // check java output manually
  Properties.writeJava(outpath, Properties.readJava(path, name))
  p.splitOn("split1").zipWithIndex.foreach(f =>
    Properties.write(outpath.replace(".properties", f._2 + ".properties"), f._1))

}