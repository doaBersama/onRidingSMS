package com.sensor.smsdetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by ASUS on 16/10/2016.
 */

public class IncomingSms extends BroadcastReceiver implements SensorEventListener {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    String messageSend = "Percobaan auto reply : Maaf sedang di jalan";
    String phoneNo = "";
    private Context context;
    private SensorManager sensorManager;

    float light = 100000;
    boolean statusKirim = false;

    int totalSample;
    float xMin, xMax, xSum, sumXpow, yMin, yMax, ySum, sumYpow, zMin, zMax, zSum, sumZpow;

    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {
            totalSample = 0;
            xSum = sumXpow = ySum = sumYpow = zSum = sumZpow = 0;
            xMin = yMin = zMin = 100;
            xMax = yMax = zMax = -100;
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);

                    statusKirim = true;
                    // Show Alert
                    int duration = Toast.LENGTH_LONG;

                    //Sending SMS back
                    phoneNo = senderNum;

                    Log.i("Status Kirim", String.valueOf(statusKirim));
                    Log.i("Light", String.valueOf(light));
                } // end for loop
                sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

                sensorManager.registerListener(this, sensorManager.getDefaultSensor
                        (Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if (totalSample < 50) {
                totalSample++;
                float tmpX = event.values[0];
                float tmpY = event.values[1];
                float tmpZ = event.values[2];

                Log.i("AC", String.valueOf(tmpX) + " " + String.valueOf(tmpY) + " " +String.valueOf(tmpZ));

                xMin = Math.min(tmpX, xMin);
                yMin = Math.min(tmpY, yMin);
                zMin = Math.min(tmpZ, zMin);

                xMax = Math.max(tmpX, xMax);
                yMax = Math.max(tmpY, yMax);
                zMax = Math.max(tmpZ, zMax);

                xSum += tmpX;
                ySum += tmpY;
                zSum += tmpZ;

                sumXpow += Math.pow(tmpX, 2);
                sumYpow += Math.pow(tmpY, 2);
                sumZpow += Math.pow(tmpZ, 2);

            } else {

                float xAverage = xSum/50;
                float yAverage = ySum/50;
                float zAverage = zSum/50;

                Float[][] dataset = {
                        {-0.40222588f,2.2409728f,0.9954133012f,-2.7581203f,1.3024458f,-0.6188532517f,6.4739213f,13.541605f,9.831549836f,1.0f},
                        {-0.40222588f,2.2409728f,1.0342951355f,-3.3327289f,1.3024458f,-0.6780379215f,6.4739213f,13.541605f,9.782325079f,1.0f},
                        {-0.4405331f,2.2026656f,1.0634086272f,-3.3327289f,0.82360536f,-0.9281841144f,6.4739213f,13.541605f,9.683109353f,1.0f},
                        {-0.4405331f,2.2026656f,1.1520898589f,-3.7924154f,0.7469909f,-1.2367488327f,7.297527f,12.066776f,9.622967032f,1.0f},
                        {-0.15322891f,2.3558946f,1.2553278342f,-4.175488f,1.4173675f,-1.2917197005f,5.592855f,12.603078f,9.461885141f,1.0f},
                        {-0.30645782f,3.0454245f,1.2819513485f,-4.3861775f,1.8004397f,-1.2229582272f,5.592855f,13.369223f,9.502490774f,1.0f},
                        {-0.30645782f,3.0454245f,1.2350249874f,-5.6886234f,1.8770541f,-1.1072703983f,5.5162406f,14.001291f,9.562250048f,1.0f},
                        {-0.05746084f,3.0454245f,1.2189359561f,-5.6886234f,1.8770541f,-1.2329180999f,5.5162406f,14.001291f,9.534285742f,1.0f},
                        {-0.40222588f,2.6240451f,1.1566867171f,-3.964798f,1.532289f,-1.3396037291f,5.746084f,12.6222315f,9.538691077f,1.0f},
                        {-0.40222588f,2.6240451f,1.2265974141f,-4.3478703f,1.5897499f,-1.0367851009f,5.746084f,13.196839f,9.549991658f,1.0f},
                        {-0.11492168f,2.585738f,1.1743080521f,-4.3478703f,1.9153614f,-1.0162907338f,5.746084f,13.196839f,9.696516826f,1.0f},
                        {0.15322891f,2.336741f,1.1030566069f,-3.6008794f,1.9153614f,-1.207060732f,6.991069f,13.196839f,9.711073592f,1.0f},
                        {0.19153613f,2.2409728f,1.0383173916f,-5.746084f,6.5505357f,-1.2403880222f,3.7924154f,16.031574f,9.730418688f,1.0f},
                        {0.19153613f,2.2409728f,1.0808384181f,-5.746084f,6.5505357f,-1.2110829891f,3.7924154f,16.031574f,9.8093316f,1.0f},
                        {0.36391866f,1.9536686f,1.0699208556f,-3.7924154f,2.1068976f,-1.1365754297f,7.297527f,12.181698f,9.647675175f,1.0f},
                        {-0.59376204f,2.6431987f,1.057471001f,-5.592855f,2.030283f,-1.1193371649f,6.4164605f,12.181698f,9.673915593f,1.0f},
                        {-0.59376204f,2.6431987f,1.0674308746f,-5.592855f,2.030283f,-1.1139741557f,6.4164605f,12.028469f,9.630628412f,1.0f},
                        {-0.59376204f,2.6431987f,1.1162725897f,-5.592855f,2.030283f,-1.2147221698f,6.4164605f,12.717999f,9.663764136f,1.0f},
                        {-1.0342951f,3.0071173f,1.302637253f,-6.090849f,3.217807f,-1.2325350291f,4.367024f,14.805743f,9.673915545f,1.0f},
                        {-1.7621324f,3.5625722f,1.3365391533f,-6.244078f,3.217807f,-1.5052824823f,4.2521024f,14.805743f,9.529688902f,1.0f},
                        {-1.7621324f,4.003105f,1.2695015044f,-8.619126f,6.033388f,-1.181394881f,4.2521024f,14.824897f,9.436985409f,1.0f},
                        {-1.6472107f,4.003105f,1.1354262067f,-8.619126f,6.033388f,-0.8477389343f,5.056554f,14.901511f,9.504214567f,1.0f},
                        {-1.2258313f,4.271256f,1.4089398158f,-7.144298f,5.554548f,-1.1936532001f,4.3861775f,16.395493f,9.520112094f,1.0f},
                        {-1.2258313f,4.271256f,1.5728947427f,-5.93762f,5.554548f,-1.3886369927f,4.3861775f,22.045809f,9.803777115f,1.0f},
                        {-1.685518f,4.4244847f,1.4112382409f,-11.9135475f,7.1251445f,-1.1206779286f,0.9959879f,22.045809f,9.73444099f,1.0f},
                        {-1.685518f,4.4244847f,1.1668381416f,-11.9135475f,7.1251445f,-1.2154883098f,0.9959879f,20.321983f,9.69824065f,1.0f},
                        {-0.47884035f,3.7541082f,1.1049719687f,-6.991069f,2.7389667f,-1.4353718031f,5.0374002f,13.752295f,9.60611184f,1.0f},
                        {-1.1109096f,2.6240451f,1.1547713625f,-10.381259f,3.86903f,-1.7334020434f,4.9416323f,13.752295f,9.57106073f,1.0f},
                        {-1.1875241f,2.9879637f,1.1722011465f,-10.381259f,3.86903f,-1.7918205655f,3.3518825f,15.591042f,9.4153418f,1.0f},
                        {-3.083732f,4.1371803f,1.0992258715f,-8.791509f,9.11712f,-1.4010868273f,2.7389667f,18.674774f,9.554780133f,1.0f},
                        {-3.083732f,4.1371803f,1.0703039177f,-7.1059904f,9.11712f,-1.1729673023f,2.7389667f,18.674774f,9.632352303f,1.0f},
                        {0.076614454f,2.815581f,1.1484506677f,-6.3781533f,2.0685902f,-1.0796892067f,6.1100025f,13.445837f,9.699773079f,1.0f},
                        {-0.7469909f,3.4284968f,1.3861470157f,-7.603985f,2.0685902f,-1.5177323551f,5.056554f,16.395493f,9.670084936f,1.0f},
                        {-1.2449849f,3.4284968f,1.3710156572f,-7.603985f,2.7389667f,-1.6508499694f,3.8881836f,16.395493f,9.47414339f,1.0f},
                        {-7.2592196f,7.1826053f,0.9630436865f,-16.74026f,5.9759274f,-8.013488845f,-9.308656f,9.040505f,1.9925503965f,0.0f},
                        {-7.2592196f,7.1826053f,0.9873687814f,-16.74026f,-3.6774938f,-9.760106884f,-3.2369606f,7.699753f,1.0798807368f,0.0f},
                        {-8.082825f,8.676587f,0.7088752412f,-16.836027f,-1.1109096f,-9.610325597f,-8.561666f,6.6654577f,0.7960241769f,0.0f},
                        {-8.082825f,9.595961f,0.990816433f,-16.836027f,-1.1109096f,-9.735781761f,-8.561666f,6.6654577f,1.0032662773f,0.0f},
                        {-6.435614f,9.595961f,2.2185630367f,-16.050728f,1.8195933f,-7.9518142153f,-4.5202527f,15.016433f,2.3503399131f,0.0f},
                        {-6.933608f,12.660539f,2.0235792657f,-20.590134f,1.8195933f,-8.5227834103f,-4.0222588f,15.016433f,2.537087634f,0.0f},
                        {-6.933608f,12.660539f,1.8272547366f,-26.02976f,1.8195933f,-8.2893008453f,-4.0222588f,15.035586f,3.0820079598f,0.0f},
                        {-5.133168f,14.5567465f,2.1835119538f,-26.02976f,3.6391866f,-6.8970246935f,-1.7621324f,16.970102f,3.3675883442f,0.0f},
                        {-5.133168f,14.5567465f,2.0490535779f,-27.945122f,3.6391866f,-8.302325365f,-2.8538885f,16.970102f,3.5392047036f,0.0f},
                        {-3.7349546f,14.5567465f,2.3758142291f,-27.945122f,3.6391866f,-6.4001800205f,-2.8538885f,16.970102f,4.5545377613f,0.0f},
                        {-6.397307f,12.717999f,1.1723926895f,-17.659632f,3.4476504f,-8.2314570088f,-4.0797195f,16.778566f,3.0793264581f,0.0f},
                        {-6.397307f,6.7229185f,0.2445916437f,-17.429789f,-3.5625722f,-9.90854743f,-4.0797195f,8.523358f,1.5679148055f,0.0f},
                        {-5.9184666f,9.595961f,1.0145669269f,-22.16073f,0.49799395f,-9.2071420877f,-4.0797195f,18.50239f,3.5424608471f,0.0f},
                        {-5.4204726f,9.883265f,1.5403336197f,-23.195026f,5.650316f,-8.3919642083f,-10.70687f,18.50239f,4.4961192741f,0.0f},
                        {-5.8801594f,9.883265f,1.239430341f,-23.195026f,6.8186865f,-8.3255011677f,-10.898406f,13.790602f,3.9985083876f,0.0f},
                        {-5.8801594f,7.5465236f,1.4315410861f,-23.195026f,6.8186865f,-8.0797603305f,-10.898406f,13.790602f,3.2848447446f,0.0f},
                        {-3.6774938f,19.000385f,1.6248010609f,-27.02575f,4.903325f,-8.5580261052f,-6.7612257f,21.35628f,2.6326641845f,0.0f},
                        {-8.734048f,19.000385f,0.6797617592f,-27.02575f,4.903325f,-8.1452657102f,-6.7612257f,21.35628f,2.1162827636f,0.0f},
                        {-8.734048f,8.1756f,0.3103304313f,-15.974113f,3.1860838f,-5.9045862225f,-5.0757074f,13.464991f,4.1470686302f,0.0f},
                        {-5.592855f,8.1756f,1.2547232962f,-8.2169f,3.1860838f,-1.5265849088f,-1.6663644f,13.464991f,8.2406447678f,0.0f},
                        {-0.21787235f,2.8054059f,1.281945376f,-3.0813377f,3.1860838f,-0.5289270438f,7.9463553f,11.143213f,9.342169068f,0.0f},
                        {0.09876082f,2.9628246f,1.2931023549f,-1.2509704f,1.2401965f,-0.5840415613f,8.523358f,10.13286f,9.28483391f,0.0f},
                        {0.09876082f,2.9628246f,1.360900161f,-3.8486793f,0.074220255f,-0.8424118385f,8.523358f,9.734225f,9.25857551f,0.0f},
                        {-0.47046062f,8.1756f,1.984709401f,-9.487623f,3.1860838f,-1.5839858904f,4.0761285f,10.953473f,8.815271114f,0.0f},
                        {-0.47046062f,8.1756f,1.9186713344f,-9.487623f,3.1860838f,-1.3728053315f,4.0761285f,11.143213f,8.849017377f,0.0f},
                        {-0.21787235f,2.44448f,1.2543881136f,-3.0813377f,1.4772224f,-0.5506065388f,7.9463553f,11.143213f,9.324900873f,0.0f},
                        {0.09876082f,2.9628246f,1.3079464055f,-1.258153f,0.7374141f,-0.6567175536f,8.523358f,10.13286f,9.29672112f,0.0f},
                        {0.09876082f,3.4979286f,1.4461935975f,-5.419874f,0.074220255f,-0.981377289f,8.06427f,9.734225f,9.21430072f,0.0f},
                        {-0.47046062f,7.4363904f,2.0487032155f,-9.487623f,0.074220255f,-2.0056244513f,4.0761285f,9.734225f,8.6735099268f,0.0f},
                        {-0.47046062f,7.4363904f,2.9628245959f,-9.487623f,0.074220255f,-3.7859759226f,4.0761285f,9.734225f,7.8156857f,0.0f}
                };
                // xmin, xmax, xav, ymin, ymax, yav, zmin, zmax, zav
                Float[] datates = {xMin, xMax, xAverage, yMin, yMax, yAverage, zMin, zMax, zAverage};

                /****************************************************
                 * Knn Start
                 */

                Comparator<Pair<String, Double>> mapComparator = new Comparator<Pair<String, Double>>() {
                    public int compare(Pair<String, Double> m1, Pair<String, Double> m2) {
                        return m1.getSecond().compareTo(m2.getSecond());
                    }
                };

                List<Pair<String, Double>> eucdis = new ArrayList<>();
                for (int i=0; i<64; i++){
                    Double tmp = Math.pow(Math.pow(dataset[i][0]-datates[0], 2)+
                            Math.pow(dataset[i][1]-datates[1], 2)+
                            Math.pow(dataset[i][2]-datates[2], 2)+
                            Math.pow(dataset[i][3]-datates[3], 2)+
                            Math.pow(dataset[i][4]-datates[4], 2)+
                            Math.pow(dataset[i][5]-datates[5], 2)+
                            Math.pow(dataset[i][6]-datates[6], 2)+
                            Math.pow(dataset[i][7]-datates[7], 2)+
                            Math.pow(dataset[i][8]-datates[8], 2), 0.5);
                    Pair<String, Double> datatmp = new Pair<String, Double>(String.valueOf(dataset[i][9]), tmp);
                    Log.i("tmp", String.valueOf(datatmp));
                    eucdis.add(datatmp);
                }

//        for (int i=0; i<eucdis.size(); i++){
//            System.out.print(String.valueOf(eucdis.get(i))+"\n");
//        }

//        System.out.print("\nStart sorting\n");
                Collections.sort(eucdis, mapComparator);
//        System.out.print("Done sorting\n");
//        System.out.print(String.valueOf(eucdis));
                for (int i=0; i<eucdis.size(); i++){
                    Log.i("First", String.valueOf(eucdis.get(i).getFirst()));
                    Log.i("Second", String.valueOf(eucdis.get(i).getSecond()));
                }

                int[] total = {0,0};
                for (int i=0; i<11; i++){
                    String className = String.valueOf(eucdis.get(i).getFirst());
                    String class0 = "0.0";
                    String class1 = "1.0";

                    Log.i("GET top 10th", String.valueOf(eucdis.get(i).getSecond()) + " == " + className);

//                    System.out.print(className + "\n");
                    if(className.equals(class0))
                        total[0]++;
                    else if(className.equals(class1))
                        total[1]++;
                }

                int maxCount = -1, className = 0;
                for (int i=0; i<2; i++){
                    Log.i("Jumlah Kelas", i + " ==> " + String.valueOf(total[i]));
//                    System.out.print("Class " + String.valueOf(i+1) + " => " + String.valueOf(total[i]) + "\n");
                    if(total[i]>maxCount){
                        maxCount = total[i];
                        className = i;
                    }
                }
                Log.i("Masuk kelas ==> ", String.valueOf(className) + "\n");

                /****************************************************
                 * Knn End
                 */

//                if(className==0)
//                    boolean onRiding = false;
                sensorManager.unregisterListener(this);

                //className 1 naik motor
                if (className==1) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, messageSend, null, null);
//                    Toast.makeText(context, "SMS sent.", Toast.LENGTH_LONG).show();
                        statusKirim = false;
//                        sensorManager.unregisterListener(this);

                    }
                    catch (Exception e) {
//                    Toast.makeText(context, "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
//                        sensorManager.unregisterListener(this);
                    }
                }

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
