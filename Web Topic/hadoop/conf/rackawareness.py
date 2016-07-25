#!/usr/bin/python
import sys
from string import join
DEFAULT_RACK = '/default/rack0';
RACK_MAP = { 
'192.168.1.10' : '/tvhadoop/rack0',
'192.168.1.11': '/tvhadoop/rack0',
'192.168.1.12' : '/tvhadoop/rack0',
'192.168.1.13': '/tvhadoop/rack0',
'192.168.1.14': '/tvhadoop/rack0',
'192.168.1.15' : '/tvhadoop/rack0',
'192.10.1.31' : '/tvhadoop/rack1',
'192.10.77.32' : '/tvhadoop/rack1',
'192.10.77.33' : '/tvhadoop/rack1',
'192.10.77.34' : '/tvhadoop/rack1'
}
if len(sys.argv)==1:
    print DEFAULT_RACK
else:
    print join([RACK_MAP.get(i, DEFAULT_RACK) for i in sys.argv[1:]]," ")