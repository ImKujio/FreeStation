package com.dxys.demo.bingo.altaotu;

import com.dxys.demo.bingo.net.NetUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import static com.dxys.demo.bingo.Utlis.log;

public class ImageGroupMannager {
    private MyOkHttp myOkHttp;
    private ArrayList<ImageGroup> imageGroups;
    private int page = 1;
    private boolean pageKay = true;
    private String baseUrl = "https://www.aitaotu.com";
    private String mainUrl;
    private boolean canLodmore = true;
    private OnImageGroupSizeChangeListener onImageGroupSizeChangeListener;
    private int index = 0;
    private static ImageGroup currentImageGroup;

    public ImageGroupMannager(String url) {
        myOkHttp = MyOkHttp.getInstence();
        imageGroups = new ArrayList<>();
        mainUrl = url;
    }

    public void addImageGroup() {
        if (pageKay) {
            myOkHttp.getHtml(getMainUrl(), new MyOkHttp.ResquestSucceedListener() {
                        @Override
                        public void onResponse(Response response) throws IOException {
                            String html = response.body().string();
                            response.body().close();
                            bindImagGroupIfo(splitStrLine(html));
                        }
                    },
                    new MyOkHttp.ResquestFailureListener() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            e.printStackTrace();
                        }
                    }
                    , mainUrl);
            pageKay = false;
        }
    }

    public void printAllImagGroupIfo() {
        if (imageGroups.size() ==0)
            log("imageGroups","is empty");

        for (ImageGroup imageGroup : imageGroups) {
            System.out.println(imageGroup.groupName + " : " + imageGroup.groupUrl + " : " + imageGroup.groupImageUrl);
        }
    }

    public ArrayList<ImageGroup> getImageGroups() {
        return imageGroups;
    }

    public static ImageGroup getCurrentImageGroup() {
        return currentImageGroup;
    }

    public static void setCurrentImageGroup(ImageGroup currentImageGroup) {
        ImageGroupMannager.currentImageGroup = currentImageGroup;
    }

    private String getMainUrl() {
        return baseUrl + noSuffix(mainUrl) + "/" + page + ".html";
    }

    private String noSuffix(String url) {
        return url.substring(0, url.length() - 5);
    }

    private void bindImagGroupIfo(String[] lines) {
        int findCuntent = 0;
        for (int i = 0; i < lines.length; ) {
            String line = lines[i];
            if (line.contains("original")) {
//                System.out.println("bind...");
                ImageGroup imageGroup = new ImageGroup();
                int currIndex = 0, endIndex = 0;
                currIndex = line.indexOf("nal=\"") + 5;
                endIndex = line.indexOf("\"", currIndex);
                imageGroup.groupImageUrl = line.substring(currIndex, endIndex);
                currIndex = line.indexOf("alt=\"", endIndex) + 5;
                endIndex = line.indexOf("\"", currIndex);
                imageGroup.groupName = line.substring(currIndex, endIndex);
                line = lines[i += 2];
                currIndex = line.indexOf("href=\"") + 6;
                endIndex = line.indexOf("\"", currIndex);
                imageGroup.groupUrl = baseUrl + line.substring(currIndex, endIndex);
                getImagesOfGroup(imageGroup);
                findCuntent++;
            }
            i++;
        }
        if (findCuntent == 0)
        {
            if (onImageGroupSizeChangeListener != null)
            onImageGroupSizeChangeListener.onEnd();
            canLodmore = false;
        }

    }

    public void getImagesOfGroup(final ImageGroup imageGroup) {
        myOkHttp.getHtml(imageGroup.groupUrl, new MyOkHttp.ResquestSucceedListener() {
            @Override
            public void onResponse(Response response) throws IOException {
                String html = response.body().string();
                response.body().close();
                getLastImageUrl(baseUrl + getLastPageUrl(splitStrLine(html)), imageGroup);
            }
        }, null,mainUrl);
    }

    public boolean isCanLodmore()
    {
        return canLodmore;
    }

    private void getLastImageUrl(String lastPageUrl, final ImageGroup imageGroup) {

        myOkHttp.getHtml(lastPageUrl, new MyOkHttp.ResquestSucceedListener() {
            @Override
            public void onResponse(Response response) throws IOException {
                String html = response.body().string();
                response.body().close();
                String lastImageUrl = null;
                for (String line : splitStrLine(html)) {
                    if (line.contains("src=\"h")) {
                        lastImageUrl = line.substring(line.indexOf("http"), line.indexOf("jpg") + 3);
                    }
                }
                bindImagesUrls(lastImageUrl, imageGroup);
            }
        }, null,mainUrl);
    }

    private void bindImagesUrls(String lastImageUrl, ImageGroup imageGroup) {
        try {
            String[] imagesUrls;
            String parentUrl;
            int size;
            if (lastImageUrl.contains("_"))
            {
                size = Integer.parseInt(lastImageUrl.substring(lastImageUrl.lastIndexOf("_") + 1, lastImageUrl.lastIndexOf(".")));
                imagesUrls = new String[size];
                parentUrl = lastImageUrl.substring(0, lastImageUrl.lastIndexOf("_") + 1);
            }else
            {
                size = Integer.parseInt(lastImageUrl.substring(lastImageUrl.lastIndexOf("/") + 1, lastImageUrl.lastIndexOf(".")));
                imagesUrls = new String[size];
                parentUrl = lastImageUrl.substring(0, lastImageUrl.lastIndexOf("/") + 1);
            }

            for (int i = 1; i <= size; i++) {
                imagesUrls[i - 1] = parentUrl + formatValue(i) + ".jpg";
            }
            imageGroup.imagesUrls = imagesUrls;

            if (index == imageGroups.size())
                imageGroups.add(imageGroup);
            else if (index < imageGroups.size())
                imageGroups.set(index,imageGroup);
            else if (index > imageGroups.size()) {
                index = imageGroups.size();
                imageGroups.add(imageGroup);
            }
            index++;

            if (onImageGroupSizeChangeListener!=null)
                onImageGroupSizeChangeListener.onChanged(page);
        } catch (NumberFormatException ex) {
            System.out.println("number format error!  "+lastImageUrl);
        }
        if (myOkHttp.isNullConnecting())
            pageKay = true;
    }

    public void addMore() {
        if (pageKay) {
            page++;
            if (onImageGroupSizeChangeListener!= null)
            {
                onImageGroupSizeChangeListener.onLoadMore();
            }
            addImageGroup();
        }
        else
        log("imagegroup manager is locked ...");
    }

    public void setlocked(Boolean key)
    {
        pageKay = key;
    }


    public int getPage() {
        return page;
    }


    public interface OnImageGroupSizeChangeListener
    {
        void onChanged(int page);
        void onEnd();
        void onLoadMore();
        void onRefreshed();
    }

    public void setOnImageGroupSizeChangeListener(OnImageGroupSizeChangeListener onImageGroupSizeChangeListener)
    {
        this.onImageGroupSizeChangeListener = onImageGroupSizeChangeListener;
    }

    private String formatValue(int i) {
        if (i < 10)
            return "0" + i;
        else return String.valueOf(i);
    }

    private String getLastPageUrl(String[] lines) {
        String lastUrl = null;
        for (String line : lines) {
            if (line.contains("末页")) {
                lastUrl = line.substring(line.lastIndexOf("=\"") + 2, line.lastIndexOf("\""));
            }
        }
        return lastUrl;
    }

    public void refreshAll()
    {
        myOkHttp.cancleAllRequest(mainUrl);
        pageKay = true;
        if (canLodmore)
        addImageGroup();
        else
        {
            if (onImageGroupSizeChangeListener != null)
                onImageGroupSizeChangeListener.onRefreshed();
        }
    }

    private String[] splitStrLine(String string) {
        return string.split("\r");
    }

}
