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
import com.jvapp.meudinheiro.activity.MainActivity
import com.jvapp.meudinheiro.activity.DespesasActivity
import com.jvapp.meudinheiro.activity.ReceitasActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.jvapp.meudinheiro.adapter.AdapterMovimentacao.MyViewHolder
import com.google.firebase.database.Exclude

class MainActivity : IntroActivity() {
    private var autenticacao: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main);
        isButtonBackVisible = false
        isButtonNextVisible = false
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorAzulVerdiadoEscuro)
                .fragment(R.layout.intro_1)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorAzulVerdiadoClaro)
                .fragment(R.layout.intro_2)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorAzulVerdiadoEscuro)
                .fragment(R.layout.intro_3)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorAzulVerdiadoClaro)
                .fragment(R.layout.intro_4)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.colorAzulVerdiadoEscuro)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        )
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    fun btEntrar(view: View?) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun btCadastrar(view: View?) {
        startActivity(Intent(this, CadastroActivity::class.java))
    }

    fun verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getAutenticacao()
        //autenticacao.signOut();
        if (autenticacao!!.currentUser != null) {
            abrirTelaPrincipal()
        }
    }

    fun abrirTelaPrincipal() {
        startActivity(Intent(this, PrincipalActivity::class.java))
    }
}