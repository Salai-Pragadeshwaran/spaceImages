package com.example.nasaimages

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var dayOfMonth = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity!!,  targetFragment!! as DatePickerDialog.OnDateSetListener ,year, month, dayOfMonth)
    }
}