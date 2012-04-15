/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gtango.util

/**
 *
 * @author hardion
 */
class DeviceDataConverter {
	
      def lambda = [
	(TangoConst.Tango_DEV_VOID):{value -> value},
        (TangoConst.Tango_DEV_BOOLEAN):{value -> value.extractBoolean()},
	(TangoConst.Tango_DEV_SHORT):{value -> value.extractShort()},
	(TangoConst.Tango_DEV_LONG):{value -> value.extractLong()},
 	(TangoConst.Tango_DEV_FLOAT):{value -> value.extractFloat()},
 	(TangoConst.Tango_DEV_DOUBLE):{value -> value.extractDouble()},
	(TangoConst.Tango_DEV_USHORT):{value -> value.extractUShort()},
	(TangoConst.Tango_DEV_ULONG):{value -> value.extractULong()},
	(TangoConst.Tango_DEV_STRING):{value -> value.extractString()},
	(TangoConst.Tango_DEVVAR_CHARARRAY):{value -> value.extractByteArray()},
	(TangoConst.Tango_DEVVAR_SHORTARRAY):{value -> value.extractShortArray()},
	(TangoConst.Tango_DEVVAR_LONGARRAY):{value -> value.extractLongArray()},
	(TangoConst.Tango_DEVVAR_FLOATARRAY):{value -> value.extractFloatArray()},
	(TangoConst.Tango_DEVVAR_DOUBLEARRAY):{value -> value.extractDoubleArray()},
	(TangoConst.Tango_DEVVAR_USHORTARRAY):{value -> value.extractUShortArray()},
	(TangoConst.Tango_DEVVAR_ULONGARRAY):{value -> value.extractULongArray()},
	(TangoConst.Tango_DEVVAR_STRINGARRAY):{value -> value.extractStringArray()},
	(TangoConst.Tango_DEVVAR_LONGSTRINGARRAY):{value -> value.extractLongStringArray()},
	(TangoConst.Tango_DEVVAR_DOUBLESTRINGARRAY):{value -> value.extractDoubleStringArray()},
	(TangoConst.Tango_DEV_STATE):{value -> value.extractDevState()},
	(TangoConst.Tango_DEV_CHAR):{value -> value.extractUChar()},
	(TangoConst.Tango_CONST_DEV_STRING):{value -> value.extractString()},
	(TangoConst.Tango_DEV_UCHAR):{value -> value.extractUChar()},
	(TangoConst.Tango_DEV_LONG64):{value -> value.extractLong64()},
	(TangoConst.Tango_DEV_ULONG64):{value -> value.extractULong64()},
	(TangoConst.Tango_DEVVAR_LONG64ARRAY):{value -> value.extractLong64Array()},
	(TangoConst.Tango_DEVVAR_ULONG64ARRAY):{value -> value.extractULong64Array()},
	(TangoConst.Tango_DEV_INT):{value -> value.extractShort()},
	(TangoConst.Tango_DEV_ENCODED):{value -> value}
    ]
  def convert(def value){
    lambda[value.getType()](value)
  }
}

