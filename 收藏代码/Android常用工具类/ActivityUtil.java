{\rtf1\ansi\ansicpg936\cocoartf1348\cocoasubrtf170
{\fonttbl\f0\fswiss\fcharset0 Helvetica;\f1\fnil\fcharset134 STHeitiSC-Light;\f2\fnil\fcharset0 Menlo-Italic;
}
{\colortbl;\red255\green255\blue255;\red98\green151\blue85;\red43\green43\blue43;\red204\green120\blue50;
\red255\green198\blue109;\red169\green183\blue198;\red152\green118\blue170;\red106\green135\blue89;}
\paperw11900\paperh16840\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\pardirnatural

\f0\i\fs30 \cf2 \cb3 /**\uc0\u8232  * 
\f1\i0 \'c5\'d0\'b6\'cf\'b5\'b1\'c7\'b0\'d3\'a6\'d3\'c3\'ca\'c7\'b7\'f1\'d4\'da\'c7\'b0\'cc\'a8\'cf\'d4\'ca\'be
\f2\i \uc0\u8232  
\f0 *\uc0\u8232  * 
\b @return\uc0\u8232  
\b0 */\uc0\u8232 
\i0 \cf4 private boolean \cf5 isTopActivity\cf6 () \{\uc0\u8232     \cf4 if \cf6 (\cf7 mContext \cf6 == \cf4 null\cf6 ) \cf4 return false;\uc0\u8232     \cf6 ActivityManager am = (ActivityManager) \cf7 mContext\cf6 .getSystemService(Context.
\i \cf7 ACTIVITY_SERVICE
\i0 \cf6 )\cf4 ;\uc0\u8232     \cf6 List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses()\cf4 ;\uc0\u8232     for \cf6 (ActivityManager.RunningAppProcessInfo processInfo : processInfos) \{\uc0\u8232         \cf4 if \cf6 (processInfo.\cf7 importance \cf6 == ActivityManager.RunningAppProcessInfo.
\i \cf7 IMPORTANCE_FOREGROUND 
\i0 \cf6 && processInfo.\cf7 processName\cf6 .equals(\cf8 "cn.yuguo.mydoctor"\cf6 ) && YuguoApplication.
\i \cf7 yuguoApplication
\i0 \cf6 .getActivity() \cf4 instanceof \cf6 WeClassMediaActivity) \{\uc0\u8232             \cf4 return true;\uc0\u8232         \cf6 \}\uc0\u8232     \}\u8232     \cf4 return false;\uc0\u8232 \cf6 \}\
}