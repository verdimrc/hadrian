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

import math
from operator import add, mul

from titus.fcn import Fcn
from titus.fcn import LibFcn
from titus.signature import Sig
from titus.signature import Sigs
from titus.datatype import *
from titus.errors import *
from titus.util import callfcn, div
import titus.P as P

provides = {}
def provide(fcn):
    provides[fcn.name] = fcn

prefix = "model.neural."

####################################################################
def matrix_vector_mult(a, b):
   return [[sum(x * b[i] for i, x in enumerate(row))][0] for row in a]

class SimpleLayers(LibFcn):
    name = prefix + "simpleLayers"
    sig = Sig([
           {"datum" : P.Array(P.Double())},
           {"model":  P.Array(P.WildRecord("M",
                      {"weights": P.Array(P.Array(P.Double())),
                       "bias"   : P.Array(P.Double())}))},
           {"activation": P.Fcn([P.Double()], P.Double())}],
               P.Array(P.Double()))
    errcodeBase = 11000
    def __call__(self, state, scope, pos, paramTypes, datum, model, activation):
        if len(model) == 0:
            raise PFARuntimeException("no layers", self.errcodeBase + 0, self.name, pos)
        # pass datum through first N - 1 layers, apply activation
        for layer in model[:-1]:
            bias = layer["bias"]
            weights = layer["weights"]
            if (len(bias) != len(weights)) or any(len(x) != len(datum) for x in weights):
                raise PFARuntimeException("weights, bias, or datum misaligned", self.errcodeBase + 1, self.name, pos)
            tmp = [sum(y) for y in zip(matrix_vector_mult(weights, datum), bias)]
            datum = [callfcn(state, scope, activation, [i]) for i in tmp]
        # pass datum through final layer, don't apply activation
        bias = model[-1]["bias"]
        weights = model[-1]["weights"]
        if (len(bias) != len(weights)) or any(len(x) != len(datum) for x in weights):
            raise PFARuntimeException("weights, bias, or datum misaligned", self.errcodeBase + 1, self.name, pos)
        return [sum(y) for y in zip(matrix_vector_mult(weights, datum), bias)]
provide(SimpleLayers())





