package exp.util

import org.scalatest._
import java.io.FileNotFoundException
import exp.util.Properties._

class PropertiesTest extends FlatSpec with Matchers {

  val path    = "src/test/resources/test.properties"
  val outpath = "target/testout.properties"
  val name    = "PropertiesTest"
  val p       = Properties.read(path, name) where ("myProp" -> "myVal")

  "Properties" should "read specified properties" in {
    p getString "prop1"      shouldBe "abc"
    p getString "someInt"    shouldBe "4"
    p getString "someDouble" shouldBe "4.0"
    p getString "split1"     shouldBe "1 to 3"
    p getString "split2"     shouldBe "{1.0,2.0,3.0,4.0,5.0}"
  }
  
  it should "cast properties correctly" in {
    p.getInt("someInt")       shouldBe 4
    p.getDouble("someDouble") shouldBe 4.0
    p.getDouble("someInt")    shouldBe 4.0  // allowed cast
    p.getList("split1") should contain allOf("1", "2", "3")
    p.getList("split2") should contain allOf("1.0", "2.0", "3.0", "4.0", "5.0")
    
  }
  
  it should "recognize inline comments" in {
    p getString "comment" shouldBe "foobar"  // comment not included
  }

  it should "add runtime properties" in {
    p getString Properties.EXPERIMENT_NAME shouldBe name
    p getString Properties.BASE_PROPERTIES shouldBe path
    p getString Properties.START_TIME
    p getString "myProp"   shouldBe "myVal"
  }

  it should "fail for missing properties" in {
    a[NoSuchElementException] shouldBe thrownBy(p getString "missing")
  }

  it should "fail for missing file" in {
    a[FileNotFoundException] shouldBe thrownBy(
      Properties.read("src/test/resources/missing.properties", name))
  }
  
  it should "fail for illegal cast" in {
    a[ClassCastException] shouldBe thrownBy(p.getList("someDouble"))
    a[NumberFormatException] shouldBe thrownBy(p.getInt("someDouble"))
  }
  
  it should "implicitly split on 'split1' but not on 'split2'" in {
    p.splitOn("split1").zipWithIndex.foreach(f => {
      f._1 getString "prop1"      shouldBe "abc"
      f._1 getString "someInt"    shouldBe "4"
      f._1 getString "someDouble" shouldBe "4.0"
      f._1 getString "split1"     shouldBe (f._2+1).toString       // split
      f._1 getString "split2"     shouldBe "{1.0,2.0,3.0,4.0,5.0}" // do not split
      f._1 getString Properties.EXPERIMENT_NAME shouldBe name
      f._1 getString Properties.BASE_PROPERTIES shouldBe path
      f._1 getString Properties.START_TIME
      f._1 getString "myProp"     shouldBe "myVal"
      a[NoSuchElementException] shouldBe thrownBy(f._1 getString "missing")
    })
  }
  
  it should "implicitly split on both 'split1' and 'split2'" in {
    p.splitOn(Seq("split1", "split2")).foreach(f => {
      f apply "prop1"      shouldBe "abc"
      f apply "someInt"    shouldBe "4"
      f apply "someDouble" shouldBe "4.0"
      f.getInt("split1")    // should not throw exception
      f.getDouble("split2") // should not throw exception
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