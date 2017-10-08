package com.dxys.demo.bingo.altaotu;

import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import static com.dxys.demo.bingo.Utlis.log;

public class TabIfoManager {

    private String mainUrl = "https://www.aitaotu.com";

    public static final String[] ALL_TAB_NAMES =
            {"AISS爱丝套图","尤果网套图","西西人体套图","推女郎套图","ROSI套图","推女神套图","头条女神套图","秀人网套图","PANS套图"
                    ,"DDY Pantyhose套图","美媛馆套图","丽柜套图","美腿宝贝套图","beautyleg套图","魅妍社套图","假面女皇套图"
                    ,"东莞V女郎套图","爱蜜社套图","RU1MM套图","3agirl套图","丝宝套图","DISI套图","蜜桃社套图","丝间舞套图"
                    ,"波萝社套图","HeiSiAi写真套图","ISHOW爱秀套图","Leghacker套图","动感之星套图","MFStar套图","赤足者套图"
                    ,"丝魅VIP套图","尤物馆套图","拍美VIP套图","FEILIN套图","克拉女神套图","唐韵套图","优星馆套图","NICE-LEG套图"
                    ,"上海炫彩摄影套图","颜女神套图","青豆客套图","美秀套图","飞图网套图","星乐园套图","Tyingart套图","影私荟套图"
                    ,"糖丝Tangs套图","希威社套图","尤蜜荟套图","51MODO杂志套图","MoKi筱嘤套图","TASTE套图","花の颜套图","中国腿模套图"
                    ,"天使攝影套图","大名模网套图","御女郎套图","丝尚写真套图","girlt果团网套图","糖果画报套图","猫萌榜套图","蜜丝俱乐部套图"};
    public static final String[] ALL_TAB_URLS =
            {"/tag/aiss.html","/tag/youguowang.html","/tag/xixiwang.html","/tag/tuinvlang.html","/tag/rosi.html"
                    ,"/tag/tuinvshen.html","/tag/ttns.html","/tag/xiurenwang.html","/tag/pansidong.html","/tag/ddy.html"
                    ,"/tag/meiyuanguan.html","/tag/ligui.html","/tag/meituibaobei.html","/tag/beautyleg.html"
                    ,"/tag/meiyanshe.html","/tag/jiamiannvhuang.html","/tag/vnvlang.html","/tag/aimishe.html"
                    ,"/tag/ruyixiezhen.html","/tag/3agirl.html","/tag/sibao.html","/tag/disi.html","/tag/mitaoshe.html"
                    ,"/tag/sijianwu.html","/tag/boluoshe.html","/tag/HeiSiAi.html","/tag/aixiu.html","/tag/Leghacker.html"
                    ,"/tag/dongganzhixing.html","/tag/MFStar.html","/tag/chizuzhe.html","/tag/simeivip.html"
                    ,"/tag/youwuguan.html","/tag/paimei.html","/tag/feilin.html","/tag/kelanvshen.html","/tag/tangyun.html"
                    ,"/tag/youxingguan.html","/tag/niceleg.html","/tag/shanghaixuancai.html","/tag/yannvshen.html"
                    ,"/tag/qingdouke.html","/tag/meixiu.html","/tag/feituwang.html","/tag/xingleyuan.html","/tag/Tyingart.html"
                    ,"/tag/yingsihui.html","/tag/tangsi.html","/tag/xiweisha.html","/tag/youmihui.html","/tag/51modozazhi.html"
                    ,"/tag/moki.html","/tag/taste.html","/tag/huayan.html","/tag/zhongguotuimo.html","/tag/tianshisheying.html"
                    ,"/tag/damingmowang.html","/tag/yunvlang.html","/tag/sishangxiezhen.html","/tag/girlt.html"
                    ,"/tag/tangguohuabao.html","/tag/maomengbang.html","/tag/msjlb.html"};

    private static ArrayList<TabIfo> tabIfos;
    private static TabIfoManager tabIfoManager;


    public static TabIfoManager getTabIfoManager()
    {
        if (tabIfoManager == null)
            tabIfoManager = new TabIfoManager();
        return tabIfoManager;
    }

    public class TabIfo
    {
        public String tabUrl;
        public String tabName;
    }

   public void addTabIfo(String tabName,String tabUrl)
   {
       TabIfo tabIfo = new TabIfo();
       tabIfo.tabName = tabName;
       tabIfo.tabUrl = tabUrl;
       tabIfos.add(tabIfo);
   }

   public static TabIfo getTabIfo(int position)
   {
       if (tabIfos == null)
       {
           tabIfoManager = new TabIfoManager();
           tabIfos = new ArrayList<>();
           tabIfoManager.init();
       }
       return position > tabIfos.size()-1 ? tabIfos.get(tabIfos.size()-1) : tabIfos.get(position);
   }

   public void printAllTab()
   {
       for (TabIfo tabIfo : tabIfos)
       {
           log(tabIfo.tabName + " : "+tabIfo.tabUrl);
//           System.out.print("\""+tabIfo.tabName+"\",");
       }
//       System.out.println();
//       for (TabIfo tabIfo : tabIfos)
//       {
//           System.out.print("\""+tabIfo.tabUrl+"\",");
//       }
   }

   public void init()
   {
       for (int i = 0;i<ALL_TAB_NAMES.length;i++)
       {
           TabIfo tabIfo = new TabIfo();
           tabIfo.tabName = ALL_TAB_NAMES[i];
           tabIfo.tabUrl = ALL_TAB_URLS[i];
           tabIfos.add(tabIfo);
       }
   }

   public void initTabIfo()
   {
       MyOkHttp myOkHttp = MyOkHttp.getInstence();
       myOkHttp.getHtml(mainUrl + "/pinpai/", new MyOkHttp.ResquestSucceedListener() {
           @Override
           public void onResponse(Response response) throws IOException {
               final String html = response.body().string();

               for (String line : splitStrLine(html))
               {
                   creatTabFromLine(line);
               }

               TabIfoManager tabIfoManager = TabIfoManager.getTabIfoManager();
               tabIfoManager.printAllTab();
           }
       }, null,mainUrl);
   }

    public void creatTabFromLine(String line)
    {
        if (line.contains("\"_self\""))
        {
            TabIfoManager tabIfoManager = TabIfoManager.getTabIfoManager();
            int curtIndex = 0,endIndex = 0;
            String title,url;
            curtIndex = line.indexOf("title=")+7;
            endIndex = line.indexOf("\"",curtIndex);
            title = line.substring(curtIndex,endIndex);
            curtIndex = line.indexOf("href=")+6;
            endIndex = line.indexOf("\"",curtIndex);
            url = line.substring(curtIndex,endIndex);
            tabIfoManager.addTabIfo(title,url);
        }
    }

    private String[] splitStrLine(String string)
    {
        return string.split("\r");
    }


}
