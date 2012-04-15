package org.gtango.util

import fr.esrf.TangoApi.DeviceAttribute
import fr.esrf.TangoApi.DeviceData
import fr.esrf.Tango.DevState
import fr.esrf.Tango.AttrQuality
import fr.esrf.Tango.DevEncoded

/**
 *
 * @author hardion
 */
class DeviceAttributeConverter {
	static lambda = [
        (DevState[].class)  :   {value -> value.extractDevStateArray()},
        (DevState.class)    :   {value -> value.extractDevState()},
        (boolean)           :   {value -> value.extractBoolean()},
        (boolean[])         :   {value -> value.extractBooleanArray()},
        (short)             :   {value -> value.extractUChar()},
        (short[])           :   {value -> value.extractUCharArray()},
        (byte[])            :   {value -> value.extractCharArray()},
        (short)             :   {value -> value.extractShort()},
        (short[])           :   {value -> value.extractShortArray()},
        (int)               :   {value -> value.extractUShort()},
        (int[])             :   {value -> value.extractUShortArray()},
        (int)               :   {value -> value.extractLong()},
        (int[])             :   {value -> value.extractLongArray()},
        (long)              :   {value -> value.extractULong()},
        (long[])            :   {value -> value.extractULongArray()},
        (long)              :   {value -> value.extractLong64()},
        (long[])            :   {value -> value.extractLong64Array()},
        (long)              :   {value -> value.extractULong64()},
        (long[])            :   {value -> value.extractULong64Array()},
        (float)             :   {value -> value.extractFloat()},
        (float[])           :   {value -> value.extractFloatArray()},
        (double)            :   {value -> value.extractDouble()},
        (double[])          :   {value -> value.extractDoubleArray()},
        (DevState)          :   {value -> value.extractState()},
        (String)            :   {value -> value.extractString()},
        (String[])          :   {value -> value.extractStringArray()},
        (AttrQuality.class)       :   {value -> value.getQuality()},
        (DevEncoded.class)        :   {value -> value.extractDevEncoded()},
        (DevEncoded[].class)      :   {value -> value.extractDevEncodedArray()}
    ]
  def convert(def type, def value){
    
  }
}

