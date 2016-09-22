#!/usr/bin/env python

# Copyright (C) 2014  Open Data ("Open Data" refers to
# one or more of the following companies: Open Data Partners LLC,
# Open Data Research LLC, or Open Data Capital LLC.)
# 
# This file is part of Hadrian.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from titus.fcn import Fcn
from titus.fcn import LibFcn
from titus.signature import Sig
from titus.signature import Sigs
from titus.datatype import *
from titus.errors import AvroException
import titus.P as P

provides = {}
def provide(fcn):
    provides[fcn.name] = fcn

prefix = "enum."

class ToString(LibFcn):
    name = prefix + "toString"
    sig = Sig([{"x": P.WildEnum("A")}], P.String())
    errcodeBase = 19000
    def __call__(self, state, scope, pos, paramTypes, x):
        if x in paramTypes[0]["symbols"]:
            return x
        else:
            raise AvroException("\"{0}\" is not in enum {1} ({2})".format(x, paramTypes[0]["name"], paramTypes[0]["symbols"]))
provide(ToString())

class ToInt(LibFcn):
    name = prefix + "toInt"
    sig = Sig([{"x": P.WildEnum("A")}], P.Int())
    errcodeBase = 19010
    def __call__(self, state, scope, pos, paramTypes, x):
        try:
            return paramTypes[0]["symbols"].index(x)
        except ValueError:
            raise AvroException("\"{0}\" is not in enum {1} ({2})".format(x, paramTypes[0]["name"], paramTypes[0]["symbols"]))
provide(ToInt())

class NumSymbols(LibFcn):
    name = prefix + "numSymbols"
    sig = Sig([{"x": P.WildEnum("A")}], P.Int())
    errcodeBase = 19020
    def __call__(self, state, scope, pos, paramTypes, x):
        return len(paramTypes[0]["symbols"])
provide(NumSymbols())
