package top.giftedcc.features;

/**
 * Created by chang on 2018/11/24.
 * Class:  地点解析类
 * describe:  根据用户当前经纬度解析地点编号
 * 方法1：在主Activity中使用SharedPreferences（sp文件），
 * 当首次安装启动应用程序的时候建立SQLite数据库，将地点信息建表导入手机数据库中。
 * 方法2：将地点信息用数组储存。
 */

public class PlaceParseHelper {

    /*
     * PlaceLongitudeInfo: All longitude of places
     * PlaceLatitudeInfo: All latitude of places
     * Lng_radius: longitude radius of each block
     * Lat_radius: latitude radius of each block
     * PlaceNumber: the number of places
     */
    private Double[] PlaceLongitudeInfo = { 112.939825, 112.939625, 112.939525,
            112.939425, 112.939325, 112.939225, 112.939125, 112.939225,
            112.939725, 112.940225, 112.940725, 112.941225, 112.941725,
            112.942225, 112.942725, 112.943225, 112.943375, 112.943375,
            112.943375, 112.943375 };
    private Double[] PlaceLatitudeInfo = { 28.178429, 28.178829, 28.179229,
            28.179629, 28.180029, 28.180429, 28.180829, 28.181229, 28.181229,
            28.181229, 28.181229, 28.181229, 28.181229, 28.181229, 28.181229,
            28.181229, 28.181629, 28.182029, 28.182429, 28.182829 };
    private Double[] Lng_radius = { 0.0003, 0.00025, 0.0003 };
    private Double[] Lat_radius = { 0.0002, 0.0002, 0.0002 };
    private int PlaceNumber = 20;

    public PlaceParseHelper() {
        super();
    }

    /*
     * 输入参数：经纬度
     * 输出参数：地点编号
     */
    public int getPlaceIdByPlaceInfo(Double lng, Double lat) {
        int placeId = -1;
        Double lat1 = 28.181029d;
        Double lat2 = 28.181429d;
        if (lat < lat1) { // 地点在第一段之内
            for (int i = 1; i <= 7; i++) {
                if ((Math.abs(lng - PlaceLongitudeInfo[i - 1]) < Lng_radius[0])
                        && (Math.abs(lat - PlaceLatitudeInfo[i - 1]) < Lat_radius[0])) {
                    placeId = i;
                }
            }
        } else if ((lat > lat1) && (lat < lat2)) { // 地点在第二段之内
            for (int i = 8; i <= 16; i++) {
                if ((Math.abs(lng - PlaceLongitudeInfo[i - 1]) < Lng_radius[1])
                        && (Math.abs(lat - PlaceLatitudeInfo[i - 1]) < Lat_radius[1])) {
                    placeId = i;
                }
            }
        } else if (lat > lat2) { // 地点在第三段之内
            for (int i = 17; i <= 20; i++) {
                if ((Math.abs(lng - PlaceLongitudeInfo[i - 1]) < Lng_radius[2])
                        && (Math.abs(lat - PlaceLatitudeInfo[i - 1]) < Lat_radius[2])) {
                    placeId = i;
                }
            }
        }
        return placeId;
    }

    /*
     * 输入参数：经纬度
     * 输出参数：子街道编号
     * -1：不在路段内
     * 1：在第一段路段内
     * 2：在第二段路段内
     * 3：在第三段路段内
     */
    public int getBlockIdByPlaceInfo(Double lng, Double lat) {
        Double lat0 = 28.178229d;
        Double lat1 = 28.181029d;
        Double lat2 = 28.181429d;
        Double lat3 = 28.183029d;
        Double lng0 = 112.938825d;
        Double lng1 = 112.940125d;
        Double lng2 = 112.938975d;
        Double lng3 = 112.943475d;
        Double lng4 = 112.943075d;
        Double lng5 = 112.943675d;
        int blockId = -1; //不在3个路段内
        if ((lat > lat0) && (lat < lat1) && (lng > lng0) && (lng < lng1)) {  //在第1个路段内
            blockId = 1;
        } else if ((lat > lat1) && (lat < lat2) && (lng > lng2) && (lng < lng3)) { //在第2个路段内
            blockId = 2;
        } else if((lat > lat2) && (lat < lat3) && (lng > lng4) && (lng < lng5)){ //在第3个路段内
            blockId = 3;
        }
        return blockId;
    }


    public int getPlaceNumber() {
        return PlaceNumber;
    }

    public void setPlaceNumber(int placeNumber) {
        PlaceNumber = placeNumber;
    }

}
