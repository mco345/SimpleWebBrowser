package com.example.simplewebbrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy{
        findViewById(R.id.refreshLayout)
    }

    private val addressBar: EditText by lazy{
        findViewById(R.id.addressBar)
    }

    private val webView: WebView by lazy{
        findViewById(R.id.webView)
    }

    private val progressBar: ContentLoadingProgressBar by lazy{
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        bindViews()
    }

    // 핸드폰 자체 뒤로가기 버튼 클릭 시
    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews(){
        webView.apply {
            webViewClient = WebViewClient() // 외부 웹브라우저 앱으로 이동 막기
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true   // 자바 스크립트 사용하겠다고 명시
            loadUrl(DEFAULT_URL)
        }

    }

    private fun bindViews(){
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        addressBar.setOnEditorActionListener{v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                val loadingUrl = v.text.toString()
                if(URLUtil.isNetworkUrl(loadingUrl)){
                    webView.loadUrl(loadingUrl)
                }else{
                    webView.loadUrl("http://$loadingUrl")
                }

            }

            return@setOnEditorActionListener false
        }

        goBackButton.setOnClickListener{
            webView.goBack()
        }

        goForwardButton.setOnClickListener{
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }

    }

    inner class WebViewClient: android.webkit.WebViewClient(){
        // 페이지 로딩 시작
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        // 페이지 로딩 끝난 뒤
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object{
        private const val DEFAULT_URL = "http://www.google.com"
    }
}