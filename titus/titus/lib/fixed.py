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
import titus.P as P

provides = {}
def provide(fcn):
    provides[fcn.name] = fcn

prefix = "fixed."

class ToBytes(LibFcn):
    name = prefix + "toBytes"
    sig = Sig([{"x": P.WildFixed("A")}], P.Bytes())
    errcodeBase = 20000
    def __call__(self, state, scope, pos, paramTypes, x):
        return x
provide(ToBytes())

class FromBytes(LibFcn):
    name = prefix + "fromBytes"
    sig = Sig([{"original": P.WildFixed("A")}, {"replacement": P.Bytes()}], P.Wildcard("A"))
    errcodeBase = 20010
    def __call__(self, state, scope, pos, paramTypes, original, replacement):
        length = min(len(original), len(replacement))
        return replacement[0:length] + original[length:len(original)]
provide(FromBytes())
