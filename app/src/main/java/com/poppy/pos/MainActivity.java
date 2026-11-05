package com.poppy.pos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String POS_BASE_URL = "https://panel.poppyapartments.com/mobil_pos.php";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        Button btnTrendyol = findViewById(R.id.btnTrendyol);
        Button btnYemeksepeti = findViewById(R.id.btnYemeksepeti);
        Button btnGrabOrder = findViewById(R.id.btnGrabOrder);
        Button btnRefresh = findViewById(R.id.btnRefresh);

        Button btnTabAdisyon = findViewById(R.id.btnTabAdisyon);
        Button btnTabTahsilat = findViewById(R.id.btnTabTahsilat);
        Button btnTabCanli = findViewById(R.id.btnTabCanli);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Açılışta doğrudan şifresiz Adisyon (Masa Açma) ekranı gelir
        webView.loadUrl(POS_BASE_URL + "?tab=adisyon");

        // Alt Menü Geçişleri
        btnTabAdisyon.setOnClickListener(v -> webView.loadUrl(POS_BASE_URL + "?tab=adisyon"));
        btnTabTahsilat.setOnClickListener(v -> webView.loadUrl(POS_BASE_URL + "?tab=tahsilat"));
        btnTabCanli.setOnClickListener(v -> webView.loadUrl(POS_BASE_URL + "?tab=canli"));

        // Üst Kısayollar: Trendyol & Yemeksepeti Sipariş Ekranları
        btnTrendyol.setOnClickListener(v -> webView.loadUrl("https://partner.tgoyemek.com"));
        btnYemeksepeti.setOnClickListener(v -> webView.loadUrl("https://partner-app.deliveryhero.com"));

        // Kritik Buton: 🚀 SİPARİŞİ AL (Sık Kullanılanlar Botunu Doğrudan Çalıştırır)
        btnGrabOrder.setOnClickListener(v -> runOrderGrabberBot());

        // Yenile
        btnRefresh.setOnClickListener(v -> webView.reload());
    }

    private void runOrderGrabberBot() {
        String botJs = "(function(){try{const API_URL=\"https://panel.poppyapartments.com/api_receiver.php\";const prc=function(t){let c=(t||'').replace(/[^0-9,.]/g,'');if(c.indexOf(',')!==-1&&c.indexOf('.')!==-1)c=c.replace(/\\./g,'').replace(',','.');else if(c.indexOf(',')!==-1)c=c.replace(',','.');return parseFloat(c)||0;};const gTxt=function(e){return e?(e.innerText||e.textContent||'').trim():'';};let h=window.location.hostname;let isTrendyol=document.querySelector('.order-details-info')!==null||document.querySelector('.order-details-content')!==null||document.querySelector('.order-details-info-content')!==null||h.indexOf(\"trendyol\")!==-1||h.indexOf(\"tgoyemek\")!==-1;let isYemeksepeti=document.querySelector('[data-testid=\"order-details-content\"]')!==null||h.indexOf(\"yemeksepeti\")!==-1||h.indexOf(\"deliveryhero\")!==-1;if(!isTrendyol&&!isYemeksepeti){alert(\"⚠️ Siparişi çekmek için önce üstten TY Panel veya YS Panel'e girip sipariş detayını açın!\");return;}let payload={source:isTrendyol?\"Trendyol\":\"Yemeksepeti\",order_id:\"\",customer_name:\"Bilinmiyor\",customer_phone:\"\",address:\"\",total_price:0,note:\"\",payment_method:\"\",created_at:\"\",items:[]};if(isTrendyol){document.querySelectorAll('.order-details-info__item').forEach(function(item){let titleEl=item.querySelector('.order-details-info__item__title');if(titleEl){let t=gTxt(titleEl);let val=gTxt(titleEl.nextElementSibling).replace('\\n','');if(t.indexOf('Müşteri İsmi')!==-1)payload.customer_name=val;else if(t.indexOf('Sipariş No')!==-1)payload.order_id=val;else if(t.indexOf('Ödeme Yöntemi')!==-1){let badge=item.querySelector('.order-payment-type__badge')||item.querySelector('.order-payment-type');payload.payment_method=badge?gTxt(badge):val;}}});if(!payload.payment_method){let payEl=document.querySelector('.order-payment-type__badge')||document.querySelector('.order-payment-type');if(payEl)payload.payment_method=gTxt(payEl);}if(!payload.order_id){let mDoc=document.body.innerText.match(/Sipariş No\\.?\\s*([0-9]{7,})/i);if(mDoc)payload.order_id=mDoc[1];}if(!payload.payment_method){let mPay=document.body.innerText.match(/(Online Kredi Kartı|Kapıda Nakit|Kapıda Kredi Kartı|Online Kredi\\/Banka Kartı)/i);if(mPay)payload.payment_method=mPay[1];}document.querySelectorAll('.order-details-price tr').forEach(function(row){let tc=row.querySelector('.price-text');if(tc&&gTxt(tc).indexOf('Toplam:')!==-1){let vc=row.querySelector('.price-value');if(vc)payload.total_price=prc(gTxt(vc));}});if(payload.total_price===0){let mPrice=document.body.innerText.match(/Toplam:\\s*([0-9.,]+)/i);if(mPrice)payload.total_price=prc(mPrice[1]);}let nc=document.querySelector('.order-note__content');if(nc)payload.note=gTxt(nc).replace('Sipariş Notu:','').trim();document.querySelectorAll('p').forEach(function(p){let txt=gTxt(p);if(txt.indexOf('numarasını arayarak')!==-1||txt.indexOf('Müşteri İletişim')!==-1){let m=txt.match(/(0\\s?\\d{3}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2})/);if(m)payload.customer_phone=m[1].replace(/\\s/g,'');}});document.querySelectorAll('.order-item--product').forEach(function(row){let nameEl=row.querySelector('.order-item--product__name span')||row.querySelector('.product__name');let itemName=nameEl?gTxt(nameEl):\"Ürün\";let countEl=row.querySelector('.order-item__count')||row.querySelector('.align-right');let itemQty=countEl?parseInt(gTxt(countEl)):1;let pEl=row.querySelector('.order-item__total-price');let itemPrice=pEl?prc(gTxt(pEl)):0;if(itemName)payload.items.push({name:itemName,qty:itemQty,price:itemPrice>0?(itemPrice/itemQty):0});});let timeEl=document.querySelector('.order-details-header__order-time strong');if(timeEl){let tStr=gTxt(timeEl);if(tStr.match(/^\\d{2}:\\d{2}$/)){let d=new Date();let parts=tStr.split(':');d.setHours(parseInt(parts[0]),parseInt(parts[1]),0);let localTime=new Date(d.getTime()-(d.getTimezoneOffset()*60000));payload.created_at=localTime.toISOString().slice(0,19).replace('T',' ');}}}else if(isYemeksepeti){let rootEl=document.querySelector('[data-testid=\"order-details-content\"]')||document.body;let shortCodeEl=document.querySelector('[data-testid=\"new-order-short-code\"]');if(shortCodeEl){payload.order_id=gTxt(shortCodeEl);let longCodeEl=shortCodeEl.nextElementSibling;if(longCodeEl)payload.order_id+=\" (\"+gTxt(longCodeEl)+\")\";}let timeStr=\"\";document.querySelectorAll('h3, h4, span, p').forEach(function(el){let t=gTxt(el);if(t.match(/^\\d{2}:\\d{2}$/))timeStr=t;});if(timeStr){let d=new Date();let parts=timeStr.split(':');d.setHours(parseInt(parts[0]),parseInt(parts[1]),0);let localTime=new Date(d.getTime()-(d.getTimezoneOffset()*60000));payload.created_at=localTime.toISOString().slice(0,19).replace('T',' ');}document.querySelectorAll('p').forEach(function(p){let txt=gTxt(p);if(txt==='Müşteri'&&p.nextElementSibling)payload.customer_name=gTxt(p.nextElementSibling);if(txt.indexOf('ödendi')!==-1||txt.indexOf('Ödendi')!==-1||txt==='CREDIT_CARD'||txt==='CASH')payload.payment_method=txt;});let phoneIcon=document.querySelector('svg.cape-phone');if(phoneIcon&&phoneIcon.parentNode&&phoneIcon.parentNode.parentNode){let pTag=phoneIcon.parentNode.parentNode.querySelector('p');if(pTag)payload.customer_phone=gTxt(pTag);}let locIcon=document.querySelector('svg.cape-location-on');if(locIcon&&locIcon.parentNode&&locIcon.parentNode.parentNode){let pTags=locIcon.parentNode.parentNode.querySelectorAll('p');let arr=[];pTags.forEach(function(pt){let t=gTxt(pt);if(t.length>0)arr.push(t);});payload.address=arr.join(' - ');}let lines=(rootEl.innerText||'').split('\\n').map(function(s){return s.trim();}).filter(function(s){return s.length>0;});let stopWords=['ara toplam','vat','kdv','toplam','kurye bahşişi','ödenecek tutar','tarafınızdan finanse','online'];for(let i=0;i<lines.length;i++){let line=lines[i];let mQty=line.match(/^(\\d+)\\s*x$/i);if(mQty&&i+1<lines.length){let qty=parseInt(mQty[1])||1;let itemName=lines[i+1];let itemPrice=0;let nextIdx=i+2;let mods=[];while(nextIdx<lines.length){let cand=lines[nextIdx];let candLow=cand.toLowerCase();if(cand.match(/^(\\d+)\\s*x$/i))break;let isStop=false;for(let sw=0;sw<stopWords.length;sw++){if(candLow.indexOf(stopWords[sw])===0||candLow.indexOf('20% off')!==-1||candLow.indexOf('% off')!==-1){isStop=true;break;}}if(isStop)break;if(cand.indexOf('₺')!==-1&&/\\d/.test(cand)&&itemPrice===0){itemPrice=prc(cand);}else if(cand.indexOf('**')===-1&&candLow.indexOf('ödendi')===-1&&candLow.indexOf('kredi')===-1){mods.push(cand);}nextIdx++;}let ln=itemName.toLowerCase();if(ln.indexOf('bahşiş')===-1&&ln.indexOf('tip')===-1&&ln.indexOf('toplam')===-1&&ln.indexOf('vat')===-1){if(mods.length>0){payload.note+=(itemName+': '+mods.join(', ')+' | ');}payload.items.push({name:itemName,qty:qty,price:itemPrice>0?(itemPrice/qty):0});}i=nextIdx-1;}if((line==='Toplam'||line==='Ödenecek tutar')&&i+1<lines.length){let pVal=prc(lines[i+1]);if(pVal>0)payload.total_price=pVal;}if(line.indexOf('**')!==-1||line.indexOf('ÇATAL')!==-1){payload.note+=line+' ';}}if(payload.total_price===0){let mTot=(rootEl.innerText||'').match(/(?:Ödenecek tutar|Toplam)[\\s\\r\\n]+₺?\\s*([0-9.,]+)/i);if(mTot)payload.total_price=prc(mTot[1]);}if(!payload.customer_phone){let mPh=(rootEl.innerText||'').match(/(\\+?90\\s?5\\d{2}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}|0?5\\d{2}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2})/);if(mPh)payload.customer_phone=mPh[1].replace(/\\s/g,'');}}if(payload.payment_method){payload.note=\"[ÖDEME: \"+payload.payment_method+\"] \"+payload.note;}let payloadStr=encodeURIComponent(JSON.stringify(payload));let pixelUrl=API_URL+\"?pixel_data=\"+payloadStr;let img=new Image();img.onload=function(){window.location.href='https://panel.poppyapartments.com/mobil_pos.php?tab=canli';};img.onerror=function(){window.location.href='https://panel.poppyapartments.com/mobil_pos.php?tab=canli';};img.src=pixelUrl;}catch(err){alert(\"❌ Bot Hatası: \"+err.message);}})();";

        webView.evaluateJavascript(botJs, null);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
