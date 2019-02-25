package com.pait.smartpos;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppSignatureHelper  extends ContextWrapper {
    public static final String TAG = "Log";

    private static final String HASH_TYPE = "SHA-256";
    public static final int NUM_HASHED_BYTES = 9;
    public static final int NUM_BASE64_CHAR = 11;

    public AppSignatureHelper(Context context) {
        super(context);
    }

    /**
     * Get all the app signatures for the current package
     *
     * @return
     */
    public ArrayList<String> getAppSignatures() {
        ArrayList<String> appCodes = new ArrayList<>();

        try {
            // Get all package signatures for the current package
            String packageName = getPackageName();
            PackageManager packageManager = getPackageManager();
            Signature[] signatures = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES).signatures;

            // For each signature create a compatible hash
            for (Signature signature : signatures) {
                //String hash = hash(packageName, signature.toCharsString());
                String hash = hash(packageName, "308203573082023fa00302010202044bd80442300d06092a864886f70d01010b0500305c310b300906035504061302494e310b3009060355040813024d48310d300b0603550407130450756e6531153013060355040a130c4c4e4220494e464f54454348310c300a060355040b13034c4e42310c300a060355040313034c4e42301e170d3137303632353038333932395a170d3432303631393038333932395a305c310b300906035504061302494e310b3009060355040813024d48310d300b0603550407130450756e6531153013060355040a130c4c4e4220494e464f54454348310c300a060355040b13034c4e42310c300a060355040313034c4e4230820122300d06092a864886f70d01010105000382010f003082010a02820101008c51acae5d7ff0b6c63bfde938295061105809a8750101dddb5870f345d5429d1b9cb67a7b1141eed9000b853e17a887afec43dec40729edfd237be005ba2f14e30020b1e0d8197d1daee80d917fbb52d5edfd74519efe1aff138ed0a13b9cb99d6b9c49d3911e7a81721783e59015f2da6bd51856666b1193c255406958c0eba6a54821097869ec25ea061b45bccbe9079f67ce0df208676970a11eddeb476f5183f653150fb79d3e0aa3be5c0f82524a8cadf84d0ce0a07f2e4181581612faec1c1333a0379f1de8abfb677d76d72aeb2b31ba92f8fffca0d5e33ccbbe1835fe03d8bcba53e3254512488c00ec9fb15e1689bbad9549b9646801435150726b0203010001a321301f301d0603551d0e04160414796ec949901ca50ed017ce0b468a91e93b321732300d06092a864886f70d01010b050003820101002c1209c2a1f1fc54ff209caf1f1992b831faad4ae689ec3056373685f3a70dd382fb99753603f4fa1341d4702e929bfed51c03cfae5e52e39606ea1a34a3c5dd2640a7000ffb818392462f2fd1bd2019fc4cd26837dba26cca7e8982a7663569a12ce957542e31c3ae644aff9d7ce442361a78239f0d4ee1aa74d9d1cc9c02d1c5e3aabec89bf892976c5933751a994020c6b5024e609d55c23fd447e819a2d50ee1c33a52a46418aa67da70d02d1d1bd65ea4a3c7fff9e684c81fb969ae9b010bc417a0904b8343954ef827109934e76eb768340fdb3a2ad8c927c4ef1a2168cc663b32ede04dfdb69e813097750ad5efabd46a1b519141cc47b2ee06de56f4");
                if (hash != null) {
                    appCodes.add(String.format("%s", hash));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to find package to obtain hash.", e);
        }
        return appCodes;
    }

    private static String hash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
            messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
            byte[] hashSignature = messageDigest.digest();

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            // encode into Base64
            String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR);

            Log.d(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash));
            return base64Hash;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "hash:NoSuchAlgorithm", e);
        }
        return null;
    }
}

