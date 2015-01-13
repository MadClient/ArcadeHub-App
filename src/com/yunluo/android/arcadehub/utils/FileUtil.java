package com.yunluo.android.arcadehub.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.save.ArchiveObj;

public class FileUtil {

    public static final String SAVE_LIST_NAME = "savelist.txt";
    public final static String SPLIT = "##";

    public static final String MAGIC_FILE = "dont-delete-00004.bin";
    public static final int BUFFER_SIZE = 1024 * 48;

    public static String GAME_FILE_PATH = null;

    public static void ChmodeFile(String path) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        File[] files = file.listFiles();
        for (File cFile : files) {
            Runtime runtime = Runtime.getRuntime();
            String s1 = (new StringBuilder()).append("chmod 777 ")
                    .append(cFile.getAbsolutePath()).toString();
            Process process = null;

            try {
                process = runtime.exec(s1);
            } catch (IOException e) {
            }

            try {
                if (process != null)
                    process.waitFor();
            } catch (InterruptedException e) {
            }
        }

    }

    public static void write(final Context context, final List<RomInfo> list) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(SAVE_LIST_NAME, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e1) {
            fos = null;
        }
        if (null == fos) {
            return;
        }

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            oos = null;
        } catch (IOException e) {
            oos = null;
        } finally {
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
                oos = null;
            }
        }
    }

    public static List<RomInfo> read(Context context) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(SAVE_LIST_NAME);
        } catch (FileNotFoundException e1) {
            fis = null;
        }
        if (null == fis) {
            return null;
        }

        List<RomInfo> list = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<RomInfo>) ois.readObject();
            ois.close();
        } catch (StreamCorruptedException e) {
            list = null;
        } catch (FileNotFoundException e) {
            list = null;
        } catch (IOException e) {
            list = null;
        } catch (ClassNotFoundException e) {
            list = null;
        }
        return list;
    }

    public static String[] fileSize(long size) {
        String str = "";
        if (size >= 1024) {
            str = "KB";
            size /= 1024;
            if (size >= 1024) {
                str = "MB";
                size /= 1024;
                if (size >= 1024) {
                    str = "GB";
                    size /= 1024;
                }
            }
        }
        DecimalFormat formatter = new DecimalFormat();
        formatter.setGroupingSize(3);
        String[] result = new String[2];
        result[0] = formatter.format(size);
        result[1] = str;
        return result;
    }

    public static String getLibDir(Context context) {
        String cache_dir, lib_dir;
        try {
            cache_dir = context.getCacheDir().getCanonicalPath();
            lib_dir = cache_dir.replace("cache", "lib");
        } catch (Exception e) {
            e.printStackTrace();
            lib_dir = "/data/data/com.yunluo.android.arcadehub/lib";
        }
        return lib_dir;
    }

    public static String getDefaultROMsDIR() {
        String res_dir = null;
        if(true == Utils.isExistSdcard()) {
            try {
                res_dir = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator+ContentValue.GAMES_PATH;
            } catch (IOException e) {
                res_dir = "sdcard/" +ContentValue.GAMES_PATH;
            }
        } else {
            res_dir = GAME_FILE_PATH + File.separator+ContentValue.GAMES_PATH;
        }

        return res_dir;
    }

    public static boolean exists(String path, String name) {
        File file = new File(path + File.separator + name + ".zip");
        return file.exists();
    }

    public static String formatFileSize(long length) {
        if(0 == length) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("##0.00");
        String target = null;
        try {
            target = df.format((float) length / 1024 / 1024);
        } catch(Exception e) {} 

        return target;
    }

    public static boolean ensureROMsDir(String roms_dir) {

        File res_dir = new File(roms_dir);

        if (res_dir.exists() == false) {
            if (!res_dir.mkdirs()) {
                return false;
            }
        }

        String str_sav_dir = roms_dir + "saves/";
        File sav_dir = new File(str_sav_dir);
        if (sav_dir.exists() == false) {

            if (!sav_dir.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    public static void deleteFile(String path) {
        if (null == path) {
            return;
        }
        try {
            File file = new File(path);
            String tmpPath = file.getAbsolutePath();
            if(null != tmpPath) {
                if(tmpPath.startsWith("/data/data")) {
                    Utils.chmodeFile(tmpPath);
                }
            }
            
            if (file.exists()) {
                if (file.isFile()) {
                   file.delete();
                }
                else if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i].toString());
                    }
                }
                file.delete();
            } 
        } catch (Exception e) {
        }
    }

    public static boolean traverse(List<ArchiveObj> list, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        String[] fileNames = file.list();
        for (String name : fileNames) {
            if (name.endsWith(".sta")) {
                int index = name.indexOf(".sta");
                String fileName = name.substring(0, index);
                Debug.e("fileName = ", ""+fileName);
                String items[] = fileName.split(SPLIT);
                if (items.length > 1) {
                    ArchiveObj mObj = new ArchiveObj();
                    mObj.setDesc(items[0]);
                    long time = Long.valueOf(items[1]);
                    String t = Utils.formatTime(time);
                    mObj.setTime(t);
                    mObj.setDate(time);
                    list.add(mObj);
                }
                Debug.e("list.size() = ", ""+list.size());
            }
        }
        return true;
    }

    public static boolean staTraverse(List<ArchiveObj> list, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        String[] fileNames = file.list();
        for (String name : fileNames) {
            ArchiveObj mObj = new ArchiveObj();
            mObj.setName(name);
            mObj.setDesc(Emulator.getGameDesc(name));
            File f = new File(path+File.separator+name);
            if (f.exists()) {
                mObj.setCount(f.list().length);
                Debug.d("Size = ", f.list().length);
            } else {
                mObj.setCount(0);
            }
            list.add(mObj);
        }
        return true;
    }

    public static void copyFiles(Context context, String path) {
        try {
            File fm = new File(path + File.separator + "saves/" + MAGIC_FILE);
            if (fm.exists())
                return;

            fm.createNewFile();

            // Create a ZipInputStream to read the zip file
            BufferedOutputStream dest = null;
            InputStream fis = context.getResources().openRawResource(R.raw.files);
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(fis));
            // Loop over all of the entries in the zip file
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {

                    String destination = path;
                    String destFN = destination + File.separator
                            + entry.getName();
                    // Write the file to the file system
                    FileOutputStream fos = new FileOutputStream(destFN);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } else {
                    File f = new File(path + File.separator
                            + entry.getName());
                    f.mkdirs();
                }

            }
            zis.close();
        } catch (Exception e) {
        }
    }

    public static void saveBitmap(Bitmap bitmap, String name) {
        try {
            File mFile = new File(ContentValue.BMP_PATH);
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            File f = new File(ContentValue.BMP_PATH, name);
            if (f.exists()) {
                f.delete();
            }

            FileOutputStream bos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    } 

    public static Bitmap getDiskBitmap(String bmName) {
        Bitmap bitmap = null;
        String pathString = ContentValue.BMP_PATH + bmName;

        try {
            File file = new File(pathString);
            if (!file.exists()) {
                return null;
            }
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public static void setGameFilePath(Context context) {
        GAME_FILE_PATH = context.getFilesDir().getAbsolutePath();
    }

}
