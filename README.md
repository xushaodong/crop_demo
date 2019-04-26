

### 修复开源android-crop库三星手机拍照后裁剪旋转的bug


##### 直接引用 croplibrary 库就可以正常使用
---
修改代码

    @Nullable
      public static File getFromMediaUri(Context context, ContentResolver resolver, Uri uri) {
          if (uri == null) return null;
          if (SCHEME_FILE.equals(uri.getScheme())) {
              return new File(uri.getPath());
          } else if (SCHEME_CONTENT.equals(uri.getScheme())) {
              Cursor cursor = null;
              try {
                  String filePath = "";
                  if(uri.toString().startsWith("content://com.google.android.gallery3d")){
                      String[] proj = {MediaStore.Images.Media.DISPLAY_NAME};
                      cursor = resolver.query(uri, proj, null, null, null);
                      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                      cursor.moveToFirst();
                      filePath = cursor.getString(column_index);
                  }else{
                      String[] proj = {MediaStore.Images.Media.DATA};
                      cursor = resolver.query(uri, proj, null, null, null);
                      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                      cursor.moveToFirst();
                      filePath = cursor.getString(column_index);
                  }
                  if (!TextUtils.isEmpty(filePath)) {
                      return new File(filePath);
                  }
              } catch (IllegalArgumentException e) {
                  // Google Drive images
                  return getFromMediaUriPfd(context, resolver, uri);
              } catch (SecurityException ignored) {
                  // Nothing we can do
              } finally {
                  if (cursor != null) cursor.close();
              }
          }
          return null;
      }

---
### 当前版本  v1.5