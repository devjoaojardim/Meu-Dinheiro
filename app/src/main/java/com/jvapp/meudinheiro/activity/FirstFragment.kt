package com.jvapp.meudinheiro.activity

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.jvapp.meudinheiro.model.Usuario
import android.os.Bundle
import com.jvapp.meudinheiro.R
import android.widget.Toast
import com.jvapp.meudinheiro.config.ConfiguracaoFirebase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.jvapp.meudinheiro.helper.Base64Custom
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.android.material.textfield.TextInputEditText
import com.jvapp.meudinheiro.model.Movimentacao
import com.google.firebase.database.DatabaseReference
import com.jvapp.meudinheiro.helper.DateUtil
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import android.content.Intent
import com.jvapp.meudinheiro.activity.PrincipalActivity
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.jvapp.meudinheiro.activity.LoginActivity
import com.jvapp.meudinheiro.activity.CadastroActivity
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import androidx.recyclerview.widget.RecyclerView
import com.jvapp.meudinheiro.adapter.AdapterMovimentacao
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.Fragment
import com.jvapp.meudinheiro.activity.MainActivity
import com.jvapp.meudinheiro.activity.DespesasActivity
import com.jvapp.meudinheiro.activity.ReceitasActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.jvapp.meudinheiro.adapter.AdapterMovimentacao.MyViewHolder
import com.google.firebase.database.Exclude

class FirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.button_first).setOnClickListener {
            NavHostFragment.findNavController(this@FirstFragment)
                .navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }
}