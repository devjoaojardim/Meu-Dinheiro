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

class ReceitasActivity : AppCompatActivity() {
    private var campoData: TextInputEditText? = null
    private var campoCategoria: TextInputEditText? = null
    private var campoDescricao: TextInputEditText? = null
    private var campoValor: EditText? = null
    private var movimentacao: Movimentacao? = null
    private val firebaseRef = ConfiguracaoFirebase.getFirebase()
    private val auth = ConfiguracaoFirebase.getAutenticacao()
    private var receitaTotal: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receitas)
        campoValor = findViewById(R.id.editValor)
        campoData = findViewById(R.id.editData)
        campoCategoria = findViewById(R.id.editCategoria)
        campoDescricao = findViewById(R.id.editDescricao)

        //Preenche o campo data com a data atual
        campoData!!.setText(DateUtil.dataAtual())

        //recupera a Receita total
        recuperarReceitaTotal()
    }

    fun salvarReceita(view: View?) {
        if (validarCampoReceita()) {
            movimentacao = Movimentacao()
            val data = campoData!!.text.toString()
            val valorRecuperado = campoValor!!.text.toString().toDouble()
            movimentacao!!.valor = valorRecuperado
            movimentacao!!.categoria = campoCategoria!!.text.toString()
            movimentacao!!.descricao = campoDescricao!!.text.toString()
            movimentacao!!.data = data
            movimentacao!!.tipo = "r"
            val receitaAtualizada = receitaTotal!! + valorRecuperado
            atualizarReceita(receitaAtualizada)
            movimentacao!!.salvar(data)
            finish()
        }
    }

    fun validarCampoReceita(): Boolean {
        val textoValor = campoValor!!.text.toString()
        val textoData = campoData!!.text.toString()
        val textoCategoria = campoCategoria!!.text.toString()
        val textoDescricao = campoDescricao!!.text.toString()
        return if (!textoValor.isEmpty()) {
            if (!textoData.isEmpty()) {
                if (!textoCategoria.isEmpty()) {
                    if (!textoDescricao.isEmpty()) {
                        true
                    } else {
                        Toast.makeText(
                            this@ReceitasActivity, "Preencha a Descrição da Receita",
                            Toast.LENGTH_LONG
                        ).show()
                        false
                    }
                } else {
                    Toast.makeText(
                        this@ReceitasActivity, "Preencha a Categoria da Receita",
                        Toast.LENGTH_LONG
                    ).show()
                    false
                }
            } else {
                Toast.makeText(
                    this@ReceitasActivity, "Preencha a Data da Receita",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
        } else {
            Toast.makeText(
                this@ReceitasActivity, "Preencha o valor da Receita",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }

    fun recuperarReceitaTotal() {
        val emailUsuario = auth.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        val usuarioRef = firebaseRef.child("usuarios").child(idUsuario!!)
        usuarioRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                receitaTotal = usuario!!.receitaTotal
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun atualizarReceita(receitaTotal: Double?) {
        val emailUsuario = auth.currentUser!!.email
        val idUsuario = Base64Custom.codificarBase64(emailUsuario)
        val usuarioRef = firebaseRef.child("usuarios").child(idUsuario!!)
        usuarioRef.child("receitaTotal").setValue(receitaTotal)
    }
}