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
import android.widget.Button
import com.jvapp.meudinheiro.activity.MainActivity
import com.jvapp.meudinheiro.activity.DespesasActivity
import com.jvapp.meudinheiro.activity.ReceitasActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.jvapp.meudinheiro.adapter.AdapterMovimentacao.MyViewHolder
import com.google.firebase.database.Exclude
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    //Atributos para entra no Aplicativo
    private var campoEmail: EditText? = null
    private var campoSenha: EditText? = null
    private var buttonEntrar: Button? = null
    private var usuario: Usuario? = null
    private var textEsqueceuSenha: TextView? = null
    private var autenticacao: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Toobar
        supportActionBar!!.title = "Login"
        //Recuperar do Layout
        campoEmail = findViewById(R.id.editEmail)
        campoSenha = findViewById(R.id.editSenha)
        buttonEntrar = findViewById(R.id.buttonEntrar)
        textEsqueceuSenha = findViewById(R.id.textEsqueceuSenha)

        //Adicionar evento de click para entrar
        buttonEntrar!!.setOnClickListener(View.OnClickListener {
            val textoEmail = campoEmail!!.text.toString()
            val textoSenha = campoSenha!!.text.toString()
            if (!textoEmail.isEmpty()) {
                if (!textoSenha.isEmpty()) {
                    usuario = Usuario()
                    usuario!!.email = textoEmail
                    usuario!!.senha = textoSenha
                    validarLogin()
                } else {
                    Toast.makeText(
                        this@LoginActivity, "Preencha a senha",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@LoginActivity, "Preencha o email",
                    Toast.LENGTH_LONG
                ).show()
            }
            //Fim da Validação
        })
        //Click para Esqueceu a senha
        textEsqueceuSenha!!.setOnClickListener(View.OnClickListener {
            autenticacao = ConfiguracaoFirebase.getAutenticacao()
            val textoEmail = campoEmail!!.text.toString()
            usuario = Usuario()
            usuario!!.email = textoEmail
            autenticacao!!.sendPasswordResetEmail(textoEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@LoginActivity, "Redefinicão de senha Envia para: $textoEmail",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        var excecao = ""
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            excecao = "Usuário não está cadastrado."
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        Toast.makeText(
                            this@LoginActivity, "Usuário não está cadastrado.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        })


        //Fim dos eventos de click
    }

    //Criar metedo para valida a entrada do usuario
    fun validarLogin() {
        autenticacao = ConfiguracaoFirebase.getAutenticacao()
        autenticacao!!.signInWithEmailAndPassword(
            usuario!!.email.toString(),
            usuario!!.senha.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                abrirTelaPrincipal()
            } else {
                var excecao = ""
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    excecao = "Usuário não está cadastrado."
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    excecao = "E-mail e senha não correspondem a um usuário cadastrado"
                } catch (e: Exception) {
                    excecao = "Erro ao fazer Login " + e.message
                    e.printStackTrace()
                }
                Toast.makeText(
                    this@LoginActivity, excecao,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //metodo para abrir a tela Principal
    fun abrirTelaPrincipal() {
        startActivity(Intent(this, PrincipalActivity::class.java))
        finish()
    }
}