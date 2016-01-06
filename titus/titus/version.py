#!/usr/bin/env python

# Copyright (C) 2014  Open Data ("Open Data" refers to
# one or more of the following companies: Open Data Partners LLC,
# Open Data Research LLC, or Open Data Capital LLC.)
# 
# This file is part of Hadrian.
# 
# Licensed under the Hadrian Personal Use and Evaluation License (PUEL);
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#     http://raw.githubusercontent.com/opendatagroup/hadrian/master/LICENSE
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

__version_info__ = ("0.8.1-post1",)

__version__ = ".".join(map(str, __version_info__))

titusVersion = __version__    # the Titus version is for informational purposes only; doesn't affect behavior

defaultPFAVersion = "0.8.1"   # the PFA version determines how Titus will interpret PFA documents (can be overridden)
                              # must always be in the form [1-9][0-9]*\.[1-9][0-9]*\.[1-9][0-9]*
