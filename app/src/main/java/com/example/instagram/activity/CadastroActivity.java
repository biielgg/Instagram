package com.example.instagram.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;

    Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        //cadastrar usuario
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if(!textoNome.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            cadastrar( usuario );

                        }else{
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o email!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void cadastrar (Usuario usuario){

        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
          usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                       if(task.isSuccessful()){

                           try{
                               progressBar.setVisibility(View.GONE);

                               // salvar dados no firebase
                               String idUsuario = task.getResult().getUser().getUid();
                               usuario.setId(idUsuario);
                               usuario.salvar();

                               //Salvar dados no profile do Firebase
                               UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                               Toast.makeText(CadastroActivity.this,
                                       "Cadastro com sucesso",
                                       Toast.LENGTH_SHORT).show();

                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                               finish();
                           }catch (Exception e){
                               e.printStackTrace();
                           }

                       } else{

                           progressBar.setVisibility(View.GONE);

                           String erroExcecao = "";
                           try{
                               throw task.getException();
                           } catch (FirebaseAuthWeakPasswordException e){
                               erroExcecao = "Digite uma senha mais forte!";
                           } catch (FirebaseAuthInvalidCredentialsException e){
                               erroExcecao = "Digite um e-mail valido.";
                           } catch (FirebaseAuthUserCollisionException e){
                               erroExcecao = "Esta conta ja foi cadastrada.";
                           } catch (Exception e) {
                               erroExcecao = "ao cadastrar usuario: " + e.getMessage();
                           }

                           Toast.makeText(CadastroActivity.this,
                                   "Erro: " + erroExcecao,
                                   Toast.LENGTH_SHORT).show();
                       }
                    }
                }
        );
    }

    public void inicializarComponentes(){
        campoNome       = findViewById(R.id.editCadastroNome);
        campoEmail      = findViewById(R.id.editCadastroEmail);
        campoSenha      = findViewById(R.id.editCadastroSenha);
        botaoCadastrar  = findViewById(R.id.buttonCadastrar);
        progressBar     = findViewById(R.id.progressCadastro);
        campoNome.requestFocus();
    }
}