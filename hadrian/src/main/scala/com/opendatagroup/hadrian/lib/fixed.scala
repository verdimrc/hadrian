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

package com.opendatagroup.hadrian.lib

import scala.collection.immutable.ListMap

import com.opendatagroup.hadrian.ast.LibFcn
import com.opendatagroup.hadrian.errors.PFARuntimeException
import com.opendatagroup.hadrian.jvmcompiler.JavaCode
import com.opendatagroup.hadrian.jvmcompiler.javaSchema

import com.opendatagroup.hadrian.ast.AstContext
import com.opendatagroup.hadrian.ast.ExpressionContext
import com.opendatagroup.hadrian.ast.FcnDef
import com.opendatagroup.hadrian.ast.FcnRef

import com.opendatagroup.hadrian.data.PFAFixed

import com.opendatagroup.hadrian.datatype.Type
import com.opendatagroup.hadrian.datatype.FcnType
import com.opendatagroup.hadrian.datatype.AvroType
import com.opendatagroup.hadrian.datatype.AvroNull
import com.opendatagroup.hadrian.datatype.AvroBoolean
import com.opendatagroup.hadrian.datatype.AvroInt
import com.opendatagroup.hadrian.datatype.AvroLong
import com.opendatagroup.hadrian.datatype.AvroFloat
import com.opendatagroup.hadrian.datatype.AvroDouble
import com.opendatagroup.hadrian.datatype.AvroBytes
import com.opendatagroup.hadrian.datatype.AvroFixed
import com.opendatagroup.hadrian.datatype.AvroString
import com.opendatagroup.hadrian.datatype.AvroEnum
import com.opendatagroup.hadrian.datatype.AvroArray
import com.opendatagroup.hadrian.datatype.AvroMap
import com.opendatagroup.hadrian.datatype.AvroRecord
import com.opendatagroup.hadrian.datatype.AvroField
import com.opendatagroup.hadrian.datatype.AvroUnion

import com.opendatagroup.hadrian.signature.Sig
import com.opendatagroup.hadrian.signature.P

package object fixed {
  private var fcns = ListMap[String, LibFcn]()
  def provides = fcns
  def provide(libFcn: LibFcn): Unit =
    fcns = fcns + Tuple2(libFcn.name, libFcn)

  val prefix = "fixed."

  ////   toBytes (ToBytes)
  class ToBytes(val pos: Option[String] = None) extends LibFcn {
    def name = prefix + "toBytes"
    def sig = Sig(List("x" -> P.WildFixed("A")), P.Bytes)
    def doc =
      <doc>
        <desc>Convert fixed-length, named bytes into arbitrary-length, anonymous bytes.</desc>
      </doc>
    def errcodeBase = 20000
    def apply(x: PFAFixed): Array[Byte] = x.bytes
  }
  provide(new ToBytes)

  ////   fromBytes (FromBytes)
  class FromBytes(val pos: Option[String] = None) extends LibFcn {
    def name = prefix + "fromBytes"
    def sig = Sig(List("original" -> P.WildFixed("A"), "replacement" -> P.Bytes), P.Wildcard("A"))
    def doc =
      <doc>
        <desc>Overlay <p>replacement</p> on top of <p>original</p>.</desc>
        <detail>If <p>replacement</p> is shorter than <p>original</p>, the bytes beyond <p>replacement</p>'s length are taken from <p>original</p>.</detail>
        <detail>If <p>replacement</p> is longer than <p>original</p>, the excess bytes are truncated.</detail>
      </doc>
    def errcodeBase = 20010
    def apply(original: PFAFixed, replacement: Array[Byte]): PFAFixed = original.overlay(replacement)
  }
  provide(new FromBytes)

}
