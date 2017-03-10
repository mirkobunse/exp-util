package exp.util

import org.scalatest._
import java.io.FileNotFoundException
import exp.util.Properties._

class PropertiesTest extends FlatSpec with Matchers {

  val path    = "src/test/resources/test.properties"
  val outpath = "target/testout.properties"
  val name    = "PropertiesTest"
  val p       = Properties.read(path, name) + ("myProp" -> "myVal")

  "Properties" should "read specified properties" in {
    p apply "prop1"      shouldBe "abc"
    p apply "someInt"    shouldBe "4"
    p apply "someDouble" shouldBe "4.0"
    p apply "split1"     shouldBe "1 to 3"
    p apply "split2"     shouldBe "{1.0,2.0,3.0,4.0,5.0}"
  }
  
  it should "cast properties correctly" in {
    p.getAsInt("someInt")       should contain(4)    // contain because Option
    p.getAsDouble("someInt")    should contain(4.0)  // cast
    p.getAsDouble("someDouble") should contain(4.0)
    p.getAsList("someDouble")   shouldBe None        // cast not possible
    p.getAsList("split1").get   should contain allOf("1", "2", "3")
    p.getAsList("split2").get   should contain allOf("1.0", "2.0", "3.0", "4.0", "5.0")
    
  }
  
  it should "recognize inline comments" in {
    p apply "comment" shouldBe "foobar"  // comment not included
  }

  it should "add runtime properties" in {
    p apply Properties.EXPERIMENT_NAME shouldBe name
    p apply Properties.BASE_PROPERTIES shouldBe path
    p apply Properties.START_TIME
    p apply "myProp"   shouldBe "myVal"
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
      f._1 apply "prop1"      shouldBe "abc"
      f._1 apply "someInt"    shouldBe "4"
      f._1 apply "someDouble" shouldBe "4.0"
      f._1 apply "split1"     shouldBe (f._2+1).toString       // split
      f._1 apply "split2"     shouldBe "{1.0,2.0,3.0,4.0,5.0}" // do not split
      f._1 apply Properties.EXPERIMENT_NAME shouldBe name
      f._1 apply Properties.BASE_PROPERTIES shouldBe path
      f._1 apply Properties.START_TIME
      f._1 apply "myProp"     shouldBe "myVal"
      a[NoSuchElementException] shouldBe thrownBy(f._1 apply "missing")
    })
  }
  
  it should "implicitly split on both 'split1' and 'split2'" in {
    p.splitOn(Seq("split1", "split2")).foreach(f => {
      f apply "prop1"      shouldBe "abc"
      f apply "someInt"    shouldBe "4"
      f apply "someDouble" shouldBe "4.0"
      f.apply("split1").toString.length shouldBe 1  // split
      f.apply("split2").toString.length shouldBe 3  // split, too  (double string: length 3)
      f apply Properties.EXPERIMENT_NAME shouldBe name
      f apply Properties.BASE_PROPERTIES shouldBe path
      f apply Properties.START_TIME
      f apply "myProp"     shouldBe "myVal"
      a[NoSuchElementException] shouldBe thrownBy(f apply "missing")
    })
  }

  // check java output manually
  JavaProperties.write(outpath, JavaProperties.read(path, name))
  
  // split and use implicit write method to write each split result
  p.splitOn(Seq("split1","split2")).zipWithIndex.foreach(f =>
    f._1.write(outpath.replace(".properties", f._2 + ".properties")))

}