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

package test.scala.lib.fixed

import scala.collection.JavaConversions._

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.opendatagroup.hadrian.jvmcompiler._
import com.opendatagroup.hadrian.errors._
import com.opendatagroup.hadrian.data._
import test.scala._

@RunWith(classOf[JUnitRunner])
class LibFixedSuite extends FlatSpec with Matchers {
  "basic access" must "convert to bytes" taggedAs(Lib, LibFixed) in {
    val engine = PFAEngine.fromYaml("""
input: {type: fixed, name: Test, size: 10}
output: bytes
action:
  fixed.toBytes: input
""").head
    new String(engine.action(engine.jsonInput(""""0123456789"""")).asInstanceOf[Array[Byte]], "utf-8") should be ("0123456789")
  }

  it must "convert from bytes" taggedAs(Lib, LibFixed) in {
    val engine = PFAEngine.fromYaml("""
input: bytes
output: {type: fixed, name: Test, size: 10}
action:
  - let:
      original:
        type: Test
        value: "0123456789"
  - fixed.fromBytes: [original, input]
""").head
    engine.action(Array[Byte]()).asInstanceOf[PFAFixed].bytes.toList should be (List[Byte](48, 49, 50, 51, 52, 53, 54, 55, 56, 57))
    engine.action(Array[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8)).asInstanceOf[PFAFixed].bytes.toList should be (List[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8, 57))
    engine.action(Array[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8, 9)).asInstanceOf[PFAFixed].bytes.toList should be (List[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
    engine.action(Array[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).asInstanceOf[PFAFixed].bytes.toList should be (List[Byte](0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
  }

}
