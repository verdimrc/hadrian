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

package test.scala.lib.map

import scala.collection.JavaConversions._

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.opendatagroup.hadrian.jvmcompiler._
import com.opendatagroup.hadrian.data._
import com.opendatagroup.hadrian.errors._
import test.scala._

@RunWith(classOf[JUnitRunner])
class LibMapSuite extends FlatSpec with Matchers {
  "basic access" must "get length" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: int
action:
  - {map.len: [input]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")) should be (5)

    val engine2 = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: int
action:
  - {map.len: [input]}
""").head
    engine2.action(engine.jsonInput("""{}""")) should be (0)
  }

  it must "get keys" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: array, items: string}
action:
  - {map.keys: [input]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAArray[String]].toVector.toSet should be (Set("a", "b", "c", "d", "e"))

    val engine2 = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: array, items: string}
action:
  - {map.keys: [input]}
""").head
    engine2.action(engine2.jsonInput("""{}""")).asInstanceOf[PFAArray[String]].toVector should be (Vector[String]())
  }

  it must "get values" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: array, items: int}
action:
  - {map.values: [input]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAArray[String]].toVector.toSet should be (Set(1, 2, 3, 4, 5))

    val engine2 = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: array, items: int}
action:
  - {map.values: [input]}
""").head
    engine2.action(engine2.jsonInput("""{}""")).asInstanceOf[PFAArray[Int]].toVector should be (Vector[Int]())
  }

  "searching" must "check contains key" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: string
output: boolean
action:
  map.containsKey:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - input
""").head
    engine.action("a").asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine.action("z").asInstanceOf[java.lang.Boolean].booleanValue should be (false)

    val engine2 = PFAEngine.fromYaml("""
input: string
output: boolean
action:
  map.containsKey:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - params: [{x: string}]
      ret: boolean
      do: {"==": [x, input]}
""").head
    engine2.action("a").asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine2.action("z").asInstanceOf[java.lang.Boolean].booleanValue should be (false)
  }

  it must "check contains value" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: int
output: boolean
action:
  map.containsValue:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - input
""").head
    engine.action(java.lang.Integer.valueOf(1)).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine.action(java.lang.Integer.valueOf(9)).asInstanceOf[java.lang.Boolean].booleanValue should be (false)

    val engine2 = PFAEngine.fromYaml("""
input: int
output: boolean
action:
  map.containsValue:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - params: [{x: int}]
      ret: boolean
      do: {"==": [x, input]}
""").head
    engine2.action(java.lang.Integer.valueOf(1)).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine2.action(java.lang.Integer.valueOf(9)).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
  }

  "manipulation" must "add key-value pairs" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: string
output: {type: map, values: int}
action:
  map.add:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - input
    - 999
""").head
    engine.action("a").asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> 999, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5))
    engine.action("z").asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5, "z" -> 999))

    val engine2 = PFAEngine.fromYaml("""
input: int
output: {type: map, values: int}
action:
  map.add:
    - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
    - input
""").head
    engine2.action(java.lang.Integer.valueOf(1)).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("BA==" -> 2, "Ag==" -> 1, "Bg==" -> 3, "Cg==" -> 5, "CA==" -> 4))
    engine2.action(java.lang.Integer.valueOf(999)).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("BA==" -> 2, "Ag==" -> 1, "Bg==" -> 3, "Cg==" -> 5, "CA==" -> 4, "zg8=" -> 999))
  }

  it must "remove keys" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: string
output: {type: map, values: int}
action:
  map.remove:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - input
""").head
    engine.action("a").asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5))
    engine.action("z").asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5))
  }

  it must "keep only certain keys" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: array, items: string}
output: {type: map, values: int}
action:
  map.only:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - input
""").head
    engine.action(engine.jsonInput("""["b", "c", "e"]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("b" -> 2, "c" -> 3, "e" -> 5))
    engine.action(engine.jsonInput("""["b", "c", "e", "z"]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("b" -> 2, "c" -> 3, "e" -> 5))
    engine.action(engine.jsonInput("""[]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map[String, java.lang.Integer]())

    val engine2 = PFAEngine.fromYaml("""
input: {type: array, items: string}
output: {type: map, values: int}
action:
  map.only:
    - {value: {}, type: {type: map, values: int}}
    - input
""").head
    engine2.action(engine2.jsonInput("""["b", "c", "e"]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map[String, java.lang.Integer]())
    engine2.action(engine2.jsonInput("""[]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map[String, java.lang.Integer]())
  }

  it must "eliminate only certain keys" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: array, items: string}
output: {type: map, values: int}
action:
  map.except:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - input
""").head
    engine.action(engine.jsonInput("""["b", "c", "e"]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> 1, "d" -> 4))
    engine.action(engine.jsonInput("""["b", "c", "e", "z"]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> 1, "d" -> 4))
    engine.action(engine.jsonInput("""[]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5))

    val engine2 = PFAEngine.fromYaml("""
input: {type: array, items: string}
output: {type: map, values: int}
action:
  map.except:
    - {value: {}, type: {type: map, values: int}}
    - input
""").head
    engine2.action(engine2.jsonInput("""["b", "c", "e"]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map[String, java.lang.Integer]())
    engine2.action(engine2.jsonInput("""[]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map[String, java.lang.Integer]())
  }

  it must "update with an overlay" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: map, values: int}
action:
  map.update:
    - {value: {"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}, type: {type: map, values: int}}
    - input
""").head
    engine.action(engine.jsonInput("""{"b": 102, "c": 103, "z": 999}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> 1, "b" -> 102, "c" -> 103, "d" -> 4, "e" -> 5, "z" -> 999))
  }

  it must "do split" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: array, items: {type: map, values: int}}
action:
  map.split: input
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3}""")).asInstanceOf[PFAArray[PFAMap[java.lang.Integer]]].toVector map {_.toMap} should be (Vector(Map("a" -> java.lang.Integer.valueOf(1)), Map("b" -> java.lang.Integer.valueOf(2)), Map("c" -> java.lang.Integer.valueOf(3))))
  }

  it must "do join" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: array, items: {type: map, values: int}}
output: {type: map, values: int}
action:
  map.join: input
""").head
    engine.action(engine.jsonInput("""[{"a": 1}, {"b": 2}, {"c": 3}]""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap should be (Map("a" -> java.lang.Integer.valueOf(1), "b" -> java.lang.Integer.valueOf(2), "c" -> java.lang.Integer.valueOf(3)))
  }

  "max/min functions" must "find numerical argmax/argmin" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - {map.argmax: [{value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}]}
""").head.action(null) should be ("2")

    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - {map.argmin: [{value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}]}
""").head.action(null) should be ("1")
  }

  it must "find object argmax/argmin" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - {map.argmax: [{value: {"0": "one", "1": "two", "2": "three", "3": "four", "4": "five", "5": "six", "6": "seven"}, type: {type: map, values: string}}]}
""").head.action(null) should be ("1")

    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - {map.argmin: [{value: {"0": "one", "1": "two", "2": "three", "3": "four", "4": "five", "5": "six", "6": "seven"}, type: {type: map, values: string}}]}
""").head.action(null) should be ("4")
  }

  it must "find user-defined argmax/argmin" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - map.argmaxLT:
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - {fcn: u.mylt}
fcns:
  mylt:
    params: [{a: double}, {b: double}]
    ret: boolean
    do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null) should be ("1")

    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - map.argmaxLT:
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - params: [{a: double}, {b: double}]
        ret: boolean
        do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null) should be ("1")

    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - map.argminLT:
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - {fcn: u.mylt}
fcns:
  mylt:
    params: [{a: double}, {b: double}]
    ret: boolean
    do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null) should be ("4")

    PFAEngine.fromYaml("""
input: "null"
output: string
action:
  - map.argminLT:
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - params: [{a: double}, {b: double}]
        ret: boolean
        do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null) should be ("4")
  }

  it must "find the top 3 numerical argmax/argmin" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - {map.argmaxN: [{value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}, 3]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("2", "6", "4"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - {map.argminN: [{value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}, 3]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("1", "5", "3"))
  }

  it must "find the top 3 object argmax/argmin" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - {map.argmaxN: [{value: {"0": "one", "1": "two", "2": "three", "3": "four", "4": "five", "5": "six", "6": "seven"}, type: {type: map, values: string}}, 3]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("1", "2", "5"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - {map.argminN: [{value: {"0": "one", "1": "two", "2": "three", "3": "four", "4": "five", "5": "six", "6": "seven"}, type: {type: map, values: string}}, 3]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("4", "3", "0"))
  }

  it must "find the top 3 user-defined argmax/argmin" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - map.argmaxNLT: 
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - 3
      - {fcn: u.mylt}
fcns:
  mylt:
    params: [{a: double}, {b: double}]
    ret: boolean
    do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("1", "5", "3"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - map.argmaxNLT: 
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - 3
      - params: [{a: double}, {b: double}]
        ret: boolean
        do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("1", "5", "3"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - map.argminNLT: 
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - 3
      - {fcn: u.mylt}
fcns:
  mylt:
    params: [{a: double}, {b: double}]
    ret: boolean
    do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("4", "0", "6"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: string}
action:
  - map.argminNLT: 
      - {value: {"0": 5.5, "1": 2.2, "2": 7.7, "3": 4.4, "4": 6.6, "5": 2.2, "6": 7.6}, type: {type: map, values: double}}
      - 3
      - params: [{a: double}, {b: double}]
        ret: boolean
        do: {"<": [{m.abs: {"-": [a, 6.2]}}, {m.abs: {"-": [b, 6.2]}}]}
""").head.action(null).asInstanceOf[PFAArray[String]].toVector should be (Vector("4", "0", "6"))
  }

  "set functions" must "do toset" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: array, items: int}
output: {type: map, values: int}
action:
  - {map.toset: [input]}
""").head
    engine.action(engine.jsonInput("""[1, 2, 3, 4, 5]""")).asInstanceOf[PFAMap[java.lang.Void]].toMap should be (Map("BA==" -> 2, "Ag==" -> 1, "Bg==" -> 3, "Cg==" -> 5, "CA==" -> 4))
  }

  it must "do fromset" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: array, items: int}
action:
  - {map.fromset: [input]}
""").head
    engine.action(engine.jsonInput("""{"BA==": 2, "Ag==": 1, "Bg==": 3, "Cg==": 5, "CA==": 4}""")).asInstanceOf[PFAArray[Int]].toVector.toSet should be (Set(1, 2, 3, 4, 5))

    val engine2 = PFAEngine.fromYaml("""
input: {type: map, values: string}
output: {type: array, items: string}
action:
  - {map.fromset: [input]}
""").head
    engine2.action(engine2.jsonInput("""{"BA==": "two", "Ag==": "one", "Bg==": "three", "Cg==": "five", "CA==": "four"}""")).asInstanceOf[PFAArray[String]].toVector.toSet should be (Set("one", "two", "three", "four", "five"))
  }

  it must "do in" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: int
output: boolean
action:
  map.in:
    - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
    - input
""").head
    engine.action(java.lang.Integer.valueOf(2)).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine.action(java.lang.Integer.valueOf(0)).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
  }

  it must "do union" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: int}
action:
  map.fromset:
    map.union:
      - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
      - {map.toset: {value: [4, 5, 6, 7, 8], type: {type: array, items: int}}}
""").head
    engine.action(null).asInstanceOf[PFAArray[Int]].toVector.toSet should be (Set(1, 2, 3, 4, 5, 6, 7, 8))
  }

  it must "do intersection" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: int}
action:
  map.fromset:
    map.intersection:
      - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
      - {map.toset: {value: [4, 5, 6, 7, 8], type: {type: array, items: int}}}
""").head
    engine.action(null).asInstanceOf[PFAArray[Int]].toVector.toSet should be (Set(4, 5))
  }

  it must "do diff" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: int}
action:
  map.fromset:
    map.diff:
      - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
      - {map.toset: {value: [4, 5, 6, 7, 8], type: {type: array, items: int}}}
""").head
    engine.action(null).asInstanceOf[PFAArray[Int]].toVector.toSet should be (Set(1, 2, 3))
  }

  it must "do symdiff" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: "null"
output: {type: array, items: int}
action:
  map.fromset:
    map.symdiff:
      - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
      - {map.toset: {value: [4, 5, 6, 7, 8], type: {type: array, items: int}}}
""").head
    engine.action(null).asInstanceOf[PFAArray[Int]].toVector.toSet should be (Set(1, 2, 3, 6, 7, 8))
  }

  it must "do subset" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: array, items: int}
output: boolean
action:
  map.subset:
    - {map.toset: input}
    - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
""").head
    engine.action(engine.jsonInput("""[1, 2, 3]""")).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine.action(engine.jsonInput("""[1, 2, 3, 999]""")).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
    engine.action(engine.jsonInput("""[888, 999]""")).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
  }

  it must "do disjoint" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: array, items: int}
output: boolean
action:
  map.disjoint:
    - {map.toset: input}
    - {map.toset: {value: [1, 2, 3, 4, 5], type: {type: array, items: int}}}
""").head
    engine.action(engine.jsonInput("""[1, 2, 3]""")).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
    engine.action(engine.jsonInput("""[1, 2, 3, 999]""")).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
    engine.action(engine.jsonInput("""[888, 999]""")).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
  }

  "functional programming" must "do map" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: string}
output: {type: map, values: int}
action:
  map.map:
    - input
    - params: [{x: string}]
      ret: int
      do: {parse.int: [x, 10]}
""").head
    engine.action(engine.jsonInput("""{"a": "1", "b": "2", "c": "3", "d": "4", "e": "5"}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5))
  }

  it must "do mapWithKey" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: string}
output: {type: map, values: int}
action:
  map.mapWithKey:
    - input
    - params: [{key: string}, {value: string}]
      ret: int
      do:
        if: {">": [key, {string: "c"}]}
        then: {+: [{parse.int: [value, 10]}, 1000]}
        else: {parse.int: [value, 10]}
""").head
    engine.action(engine.jsonInput("""{"a": "1", "b": "2", "c": "3", "d": "4", "e": "5"}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 1004, "e" -> 1005))
  }

  it must "do filter" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: map, values: int}
action:
  map.filter:
    - input
    - params: [{x: int}]
      ret: boolean
      do: {"<": [x, 3]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("a" -> 1, "b" -> 2))
  }

  it must "do filterWithKey" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: map, values: int}
action:
  map.filterWithKey:
    - input
    - params: [{key: string}, {value: int}]
      ret: boolean
      do: {"&&": [{"<": [value, 3]}, {"==": [key, {string: "a"}]}]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("a" -> 1))
  }

  it must "do filterMap" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: map, values: int}
action:
  map.filterMap:
    - input
    - params: [{value: int}]
      ret: [int, "null"]
      do:
        if: {"==": [{"%": [value, 2]}, 0]}
        then: {"+": [value, 1000]}
        else: null
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("b" -> 1002, "d" -> 1004))
  }

  it must "do filterMapWithKey" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: map, values: int}
action:
  map.filterMapWithKey:
    - input
    - params: [{key: string}, {value: int}]
      ret: [int, "null"]
      do:
        if: {"&&": [{"==": [{"%": [value, 2]}, 0]}, {"==": [key, {string: "b"}]}]}
        then: {"+": [value, 1000]}
        else: null
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("b" -> 1002))
  }

  it must "do flatMap" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: map, values: int}
action:
  map.flatMap:
    - input
    - params: [{value: int}]
      ret: {type: map, values: int}
      do:
        if: {">": [value, 2]}
        then:
          - let: {out: {value: {}, type: {type: map, values: int}}}
          - set:
              out:
                map.add:
                  - out
                  - {s.int: value}
                  - value
          - set:
              out:
                map.add:
                  - out
                  - {s.concat: [{s.int: value}, {s.int: value}]}
                  - value
          - out
        else:
          {value: {}, type: {type: map, values: int}}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("3" -> 3, "4" -> 4, "5" -> 5, "33" -> 3, "44" -> 4, "55" -> 5))
  }

  it must "do flatMapWithKey" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: {type: map, values: int}
action:
  map.flatMapWithKey:
    - input
    - params: [{key: string}, {value: int}]
      ret: {type: map, values: int}
      do:
        map.add:
          - map.add:
              - {value: {}, type: {type: map, values: int}}
              - key
              - value
          - {s.concat: [key, key]}
          - {+: [100, value]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[PFAMap[java.lang.Integer]].toMap map {case (k, v) => (k, v.intValue)} should be (Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5, "aa" -> 101, "bb" -> 102, "cc" -> 103, "dd" -> 104, "ee" -> 105))
  }

  it must "do zipmap" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: {type: map, values: string}
action:
  map.zipmap:
    - {value: {"0": "x", "1": "y", "2": "z"}, type: {type: map, values: string}}
    - {value: {"0": 101, "1": 102, "2": 103}, type: {type: map, values: int}}
    - params: [{a: string}, {b: int}]
      ret: string
      do: {s.concat: [a, {s.int: b}]}
""").head.action(null).asInstanceOf[PFAMap[String]].toMap should be (Map("0" -> "x101", "1" -> "y102", "2" -> "z103"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: map, values: string}
action:
  map.zipmap:
    - {value: {"0": "x", "1": "y", "2": "z"}, type: {type: map, values: string}}
    - {value: {"0": 101, "1": 102, "2": 103}, type: {type: map, values: int}}
    - {value: {"0": "a", "1": "b", "2": "c"}, type: {type: map, values: string}}
    - params: [{a: string}, {b: int}, {c: string}]
      ret: string
      do: {s.concat: [{s.concat: [a, {s.int: b}]}, c]}
""").head.action(null).asInstanceOf[PFAMap[String]].toMap should be (Map("0" -> "x101a", "1" -> "y102b", "2" -> "z103c"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: map, values: string}
action:
  map.zipmap:
    - {value: {"0": "x", "1": "y", "2": "z"}, type: {type: map, values: string}}
    - {value: {"0": 101, "1": 102, "2": 103}, type: {type: map, values: int}}
    - {value: {"0": "a", "1": "b", "2": "c"}, type: {type: map, values: string}}
    - {value: {"0": true, "1": false, "2": true}, type: {type: map, values: boolean}}
    - params: [{a: string}, {b: int}, {c: string}, {d: boolean}]
      ret: string
      do: {s.concat: [{s.concat: [{s.concat: [a, {s.int: b}]}, c]}, {if: d, then: {string: "-up"}, else: {string: "-down"}}]}
""").head.action(null).asInstanceOf[PFAMap[String]].toMap should be (Map("0" -> "x101a-up", "1" -> "y102b-down", "2" -> "z103c-up"))
  }

  it must "do zipmapWithKey" taggedAs(Lib, LibMap) in {
    PFAEngine.fromYaml("""
input: "null"
output: {type: map, values: string}
action:
  map.zipmapWithKey:
    - {value: {"0": "x", "1": "y", "2": "z"}, type: {type: map, values: string}}
    - {value: {"0": 101, "1": 102, "2": 103}, type: {type: map, values: int}}
    - params: [{k: string}, {a: string}, {b: int}]
      ret: string
      do: {s.concat: [{s.concat: [k, a]}, {s.int: b}]}
""").head.action(null).asInstanceOf[PFAMap[String]].toMap should be (Map("0" -> "0x101", "1" -> "1y102", "2" -> "2z103"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: map, values: string}
action:
  map.zipmapWithKey:
    - {value: {"0": "x", "1": "y", "2": "z"}, type: {type: map, values: string}}
    - {value: {"0": 101, "1": 102, "2": 103}, type: {type: map, values: int}}
    - {value: {"0": "a", "1": "b", "2": "c"}, type: {type: map, values: string}}
    - params: [{k: string}, {a: string}, {b: int}, {c: string}]
      ret: string
      do: {s.concat: [{s.concat: [{s.concat: [k, a]}, {s.int: b}]}, c]}
""").head.action(null).asInstanceOf[PFAMap[String]].toMap should be (Map("0" -> "0x101a", "1" -> "1y102b", "2" -> "2z103c"))

    PFAEngine.fromYaml("""
input: "null"
output: {type: map, values: string}
action:
  map.zipmapWithKey:
    - {value: {"0": "x", "1": "y", "2": "z"}, type: {type: map, values: string}}
    - {value: {"0": 101, "1": 102, "2": 103}, type: {type: map, values: int}}
    - {value: {"0": "a", "1": "b", "2": "c"}, type: {type: map, values: string}}
    - {value: {"0": true, "1": false, "2": true}, type: {type: map, values: boolean}}
    - params: [{k: string}, {a: string}, {b: int}, {c: string}, {d: boolean}]
      ret: string
      do: {s.concat: [{s.concat: [{s.concat: [{s.concat: [k, a]}, {s.int: b}]}, c]}, {if: d, then: {string: "-up"}, else: {string: "-down"}}]}
""").head.action(null).asInstanceOf[PFAMap[String]].toMap should be (Map("0" -> "0x101a-up", "1" -> "1y102b-down", "2" -> "2z103c-up"))
  }

  it must "do corresponds" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: boolean
action:
  map.corresponds:
    - input
    - {value: {"a": "1", "b": "2", "c": "3", "d": "4", "e": "5"}, type: {type: map, values: string}}
    - params: [{x: int}, {y: string}]
      ret: boolean
      do: {"==": [x, {parse.int: [y, 10]}]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine.action(engine.jsonInput("""{"a": 111, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
  }

  it must "do correspondsWithKey" taggedAs(Lib, LibMap) in {
    val engine = PFAEngine.fromYaml("""
input: {type: map, values: int}
output: boolean
action:
  map.correspondsWithKey:
    - input
    - {value: {"a": "1", "b": "2", "c": "3", "d": "4", "e": "5"}, type: {type: map, values: string}}
    - params: [{k: string}, {x: int}, {y: string}]
      ret: boolean
      do:
        if: {"==": [k, {string: "a"}]}
        then: true
        else: {"==": [x, {parse.int: [y, 10]}]}
""").head
    engine.action(engine.jsonInput("""{"a": 1, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine.action(engine.jsonInput("""{"a": 111, "b": 2, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[java.lang.Boolean].booleanValue should be (true)
    engine.action(engine.jsonInput("""{"a": 1, "b": 222, "c": 3, "d": 4, "e": 5}""")).asInstanceOf[java.lang.Boolean].booleanValue should be (false)
  }

}
