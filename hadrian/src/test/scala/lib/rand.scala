// Copyright (C) 2014  Open Data ("Open Data" refers to
// one or more of the following companies: Open Data Partners LLC,
// Open Data Research LLC, or Open Data Capital LLC.)
// 
// This file is part of Hadrian.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package test.scala.lib.rand

import scala.collection.JavaConversions._

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.opendatagroup.hadrian.data._
import com.opendatagroup.hadrian.jvmcompiler._
import com.opendatagroup.hadrian.errors._
import test.scala._

@RunWith(classOf[JUnitRunner])
class LibRandSuite extends FlatSpec with Matchers {
  "numerical rand engine" must "generate random integers" taggedAs(Lib, LibRand) in {
    val engine1 = PFAEngine.fromYaml("""
input: "null"
output: int
randseed: 12345
action: {rand.int: []}
""").head
    engine1.action(null) should be (1553932502)
    engine1.action(null) should be (-2090749135)
    engine1.action(null) should be (-287790814)

    val engine2 = PFAEngine.fromYaml("""
input: "null"
output: int
randseed: 12345
action: {rand.int: [5, 10]}
""").head
    engine2.action(null) should be (6)
    engine2.action(null) should be (5)
    engine2.action(null) should be (6)
  }

  it must "generate random longs" taggedAs(Lib, LibRand) in {
    val engine1 = PFAEngine.fromYaml("""
input: "null"
output: long
randseed: 12345
action: {rand.long: []}
""").head
    engine1.action(null) should be (6674089274190705457L)
    engine1.action(null) should be (-1236052134575208584L)
    engine1.action(null) should be (-3078921119283744887L)

    val engine2 = PFAEngine.fromYaml("""
input: "null"
output: long
randseed: 12345
action: {rand.long: [5, 10]}
""").head
    engine2.action(null) should be (7)
    engine2.action(null) should be (9)
    engine2.action(null) should be (7)
  }

  it must "generate random floats" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: float
randseed: 12345
action: {rand.float: [5, 10]}
""").head
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (6.8090153 +- 0.00001)
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (7.5660477 +- 0.00001)
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (9.664968 +- 0.00001)
  }

  it must "generate random doubles" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: double
randseed: 12345
action: {rand.double: [5, 10]}
""").head
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (6.809015535802359 +- 0.00001)
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (9.664967426442704 +- 0.00001)
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (9.165456744855119 +- 0.00001)
  }

  "categorical rand engine" must "make a random choice" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input:
  type: array
  items: string
output: string
randseed: 12345
action: {rand.choice: input}
""").head
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))) should be ("two")
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))) should be ("one")
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))) should be ("two")
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))) should be ("four")
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))) should be ("one")
  }

  it must "make random choices with replacement" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input:
  type: array
  items: string
output:
  type: array
  items: string
randseed: 12345
action: {rand.choices: [3, input]}
""").head
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("two", "one", "two"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("four", "one", "five"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("one", "three", "two"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("five", "three", "three"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("one", "two", "three"))
  }

  it must "make random samples without replacement" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input:
  type: array
  items: string
output:
  type: array
  items: string
randseed: 12345
action: {rand.sample: [3, input]}
""").head
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("one", "two", "four"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("one", "three", "five"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("two", "three", "five"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("one", "two", "three"))
    engine.action(PFAArray.fromVector(Vector("one", "two", "three", "four", "five"))).asInstanceOf[PFAArray[String]].toVector should be (Vector("three", "four", "five"))
  }

  it must "make random deviates from a histogram" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: int
randseed: 12345
action: {rand.histogram: {value: [3.3, 2.2, 5.5, 0.0, 1.1, 8.8], type: {type: array, items: double}}}
""").head
    val results = 0 until 1000000 map {i => engine.action(null)}
    results.count(_ == 0) / 1000000.0 should be (0.15789473684210525 +- 0.001)
    results.count(_ == 1) / 1000000.0 should be (0.10526315789473686 +- 0.001)
    results.count(_ == 2) / 1000000.0 should be (0.26315789473684215 +- 0.001)
    results.count(_ == 3) / 1000000.0 should be (0.0 +- 0.001)
    results.count(_ == 4) / 1000000.0 should be (0.05263157894736843 +- 0.001)
    results.count(_ == 5) / 1000000.0 should be (0.42105263157894746 +- 0.001)
    results.count(_ == 6) / 1000000.0 should be (0.0 +- 0.001)
  }

  it must "make random deviates from a histogram with the other signature" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: HistogramItem
randseed: 12345
cells:
  hist:
    type:
      type: array
      items:
        type: record
        name: HistogramItem
        fields:
          - {name: label, type: string}
          - {name: prob, type: double}
    init:
      - {label: A, prob: 3.3}
      - {label: B, prob: 2.2}
      - {label: C, prob: 5.5}
      - {label: D, prob: 0.0}
      - {label: E, prob: 1.1}
      - {label: F, prob: 8.8}
action: {rand.histogram: {cell: hist}}
""").head
    val results = 0 until 1000000 map {i => engine.action(null)}
    results.count(_.asInstanceOf[PFARecord].get("label") == "A") / 1000000.0 should be (0.15789473684210525 +- 0.001)
    results.count(_.asInstanceOf[PFARecord].get("label") == "B") / 1000000.0 should be (0.10526315789473686 +- 0.001)
    results.count(_.asInstanceOf[PFARecord].get("label") == "C") / 1000000.0 should be (0.26315789473684215 +- 0.001)
    results.count(_.asInstanceOf[PFARecord].get("label") == "D") / 1000000.0 should be (0.0 +- 0.001)
    results.count(_.asInstanceOf[PFARecord].get("label") == "E") / 1000000.0 should be (0.05263157894736843 +- 0.001)
    results.count(_.asInstanceOf[PFARecord].get("label") == "F") / 1000000.0 should be (0.42105263157894746 +- 0.001)
    results.count(_.asInstanceOf[PFARecord].get("label") == "G") / 1000000.0 should be (0.0 +- 0.001)
  }

  it must "generate random strings" taggedAs(Lib, LibRand) in {
    val engine1 = PFAEngine.fromYaml("""
input: "null"
output: string
randseed: 12345
action: {rand.string: [10]}
""").head
    engine1.action(null) should be ("㽏彴爜뛞謪ᕼ㻇鬜䣚")
    engine1.action(null) should be ("瓔ꣃ잘젹랴⭺篦杌⤪晀")
    engine1.action(null) should be ("鈩睧ᤜ꺊뎇頀鴢豏荴薱")

    val engine2 = PFAEngine.fromYaml("""
input: "null"
output: string
randseed: 12345
action: {rand.string: [10, {string: "abcdefghijklmnopqrstuvwxyz0123456789"}]}
""").head
    engine2.action(null) should be ("5e7a5ehsz7")
    engine2.action(null) should be ("fg886qlti1")
    engine2.action(null) should be ("352quv3x91")

    val engine3 = PFAEngine.fromYaml("""
input: "null"
output: string
randseed: 12345
action: {rand.string: [10, 33, 127]}
""").head
    engine3.action(null) should be ("HaRmFM:wT*")
    engine3.action(null) should be ("TO9!5i68Q4")
    engine3.action(null) should be ("V\"W/%>46<z")
  }

  it must "generate random bytes" taggedAs(Lib, LibRand) in {
    val engine1 = PFAEngine.fromYaml("""
input: "null"
output: bytes
randseed: 12345
action: {rand.bytes: [10]}
""").head
    engine1.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(-42, 32, -97, 92, 49, -77, 97, -125, 34, -87))
    engine1.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(120, 7, -56, -22, -114, 121, 69, -43, -119, -17))
    engine1.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(127, -22, -109, 83, 100, 64, -22, 31, -101, 73))

    val engine2 = PFAEngine.fromYaml("""
input: "null"
output: bytes
randseed: 12345
action: {rand.bytes: [10, {base64: "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXowMTIzNDU2Nzg5"}]}
""").head
    engine2.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(53, 101, 55, 97, 53, 101, 104, 115, 122, 55))
    engine2.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(102, 103, 56, 56, 54, 113, 108, 116, 105, 49))
    engine2.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(51, 53, 50, 113, 117, 118, 51, 120, 57, 49))

    val engine3 = PFAEngine.fromYaml("""
input: "null"
output: bytes
randseed: 12345
action: {rand.bytes: [10, 33, 127]}
""").head
    engine3.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(72, 97, 82, 109, 70, 77, 58, 119, 84, 42))
    engine3.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(84, 79, 57, 33, 53, 105, 54, 56, 81, 52))
    engine3.action(null).asInstanceOf[Array[Byte]].toVector should be (Vector(86, 34, 87, 47, 37, 62, 52, 54, 60, 122))
  }

  it must "generate random UUIDs" taggedAs(Lib, LibRand) in {
    val engine1 = PFAEngine.fromYaml("""
input: "null"
output: string
randseed: 12345
action: {rand.uuid4: []}
""").head
    engine1.action(null) should be ("5c9f20d5-8361-4331-8ed8-a921eac80778")
    engine1.action(null) should be ("d545798e-09a4-4f89-8393-ea7f1fea4064")
    engine1.action(null) should be ("3c4b499b-090b-46fa-895f-9fb9fcf0e5bc")

    val engine2 = PFAEngine.fromYaml("""
input: "null"
output: string
action: {s.substr: [{rand.uuid4: []}, 14, 15]}
""").head
    for (i <- 0 until 1000)
      engine2.action(null) should be ("4")

    val engine3 = PFAEngine.fromYaml("""
input: "null"
output: string
action: {s.substr: [{rand.uuid4: []}, 19, 20]}
""").head
    for (i <- 0 until 1000)
      engine3.action(null) should be ("8")
  }

  "random distributions" must "generate Gaussian deviates" taggedAs(Lib, LibRand) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: double
randseed: 12345
action: {rand.gaussian: [10, 2]}
""").head
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (9.624382020682177 +- 0.00001)
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (11.176872610230959 +- 0.00001)
    engine.action(null).asInstanceOf[java.lang.Number].doubleValue should be (11.897609560880085 +- 0.00001)
  }

}
