package com.aswin.excerssitant.utils

import android.widget.TextView
import org.w3c.dom.ProcessingInstruction
import kotlin.math.abs

class Counter( val instruction: TextView, val count : TextView) {
    var flag = "up"
    var reps = 0
    var dif = 0
    fun push_ups(output : FloatArray, height:Int,){
        if(flag == "up"){
            if(output.get(15)*height > (output.get(21)*height+dif)){
                flag == "down"
                instruction.text = "GOOD JOB, now go up"
            }

        }
        else if(flag == "down"){
            if(output.get(15)*height < (output.get(21)*height+100)){
                flag == "up"
                reps +=1
                instruction.text = "GOOD JOB, now go down"
            }
            else{
                instruction.text = "Go higher"
            }
        }
        count.text = reps.toString()
    }
}