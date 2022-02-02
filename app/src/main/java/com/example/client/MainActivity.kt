package com.example.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.ssl.SSLContextBuilder

class MainActivity : AppCompatActivity() {

    private var switchHttp : SwitchCompat? = null;
    private var  editText_login : EditText? = null;
    private var  editText_password : EditText? = null;
    private var  textView_itog : TextView? = null;
    private var  button_log_in : Button? = null;
    private val ms = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchHttp = findViewById(R.id.switchHttp);
        editText_login = findViewById(R.id.editText_login);
        editText_password = findViewById(R.id.editText_password);
        textView_itog = findViewById(R.id.textView_itog);
        button_log_in = findViewById(R.id.button_log_in);
        button_log_in?.setOnClickListener {

            if(switchHttp?.isChecked() == true){

            //---------------------HTTPS----------------------

                val sslclient = HttpClient(Apache){
                    engine {
                        customizeClient {
                            setSSLContext(
                                SSLContextBuilder
                                    .create()
                                    .loadTrustMaterial(TrustSelfSignedStrategy())
                                    .build()
                            )
                            setSSLHostnameVerifier(NoopHostnameVerifier())
                        }
                    }
                }
                val login: String = editText_login?.text.toString()
                val password: String = editText_password?.text.toString()
                val url: String = "https://192.168.0.101:8443/api/users"
                ms.launch {
                    var resp: String = sslclient.post(url) {
                        body = FormDataContent(Parameters.build {
                            append("login", login)
                            append("password", password)
                        })
                    }
                    val servereqest: String = resp
                    textView_itog?.text = servereqest
                    sslclient.close()
                }
            }
            else {
            //---------------------HTTP----------------------

                val client = HttpClient()
                val login: String = editText_login?.text.toString()
                val password: String = editText_password?.text.toString()
                val url: String = "http://192.168.0.101:8080/api/users"
                ms.launch {
                    var resp: String  = client.post(url) {
                        body = FormDataContent(Parameters.build {
                            append("login", login)
                            append("password", password)
                        })
                    }
                    val servereqest: String = resp
                    textView_itog?.text = servereqest
                    client.close()
                }
            }
        }
    }
}