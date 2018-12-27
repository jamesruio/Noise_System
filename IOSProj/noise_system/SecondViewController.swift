//
//  SecondViewController.swift
//  noise_system
//
//  Created by JamesRuio on 2018/11/22.
//  Copyright © 2018年 JamesRuio. All rights reserved.
//
import Alamofire
import UIKit
import CoreLocation
import AVFoundation
class SecondViewController: UIViewController,CLLocationManagerDelegate{
    var navigationBar:UINavigationBar?
    var timer:Timer!  //定时器
    var flag=1       //用来标识使用说明按钮的状态
    var currentLocation:CLLocation!
    let locationManager:CLLocationManager = CLLocationManager()
    var lab=UILabel()    //显示当前分贝
    var lab1=UILabel()   //显示当前位置（经纬度）
    var lab2=UILabel()   //显示当前时间
    var lab3=UILabel()   //显示上次上传时间
    var recorder: AVAudioRecorder?
    let file_path = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first?.appending("/record.wav")
    let rect9 = CGRect(x: 10, y: 80, width: 355, height: 530)
    var view6 = UIView()     //使用说明视图
    var dict:[String:String]=[:]
    var num=1
    var currenttime:String?=nil
    var uploadtime:String?=nil
    var latitude: String?=nil
    var longitude:String?=nil
    var dBvag:String?=nil
    var phonemodel:String?=nil
    var IMEI:String?
    var myprotocol:String="IPV4"
    let url:String="http://172.20.10.7:8080/noise_collector1.0/StorenoisedataServlet"

//    var lab4=UILabel()
    override func viewDidLoad() {
        super.viewDidLoad()
        //每隔一秒刷新一次当前时间
 //        timer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(GetCurrentTime), userInfo: nil, repeats: true)
        GetLocation()
        BeginRecord()
        timer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(getData), userInfo: nil, repeats: true)
//        GetLocation()
 
        //每隔一秒获取一次分贝
 //       timer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(GetDecibels), userInfo: nil, repeats: true)
        self.title="主页"
        self.tabBarItem.image=UIImage(named:"home")?.withRenderingMode(.alwaysOriginal)
        self.view.backgroundColor=UIColor.white
        // Do any additional setup after loading
        // Do any additional setup after loading the view.
       
        //一个包含全屏幕的UIView，设置背景颜色为浅灰
        let rect1 = CGRect(x:0, y:50, width: 375, height: 600)
        let view1 = UIView(frame:rect1)
        view1.backgroundColor = UIColor.lightGray
        self.view.addSubview(view1)
        
        //一个包含分贝和位置信息显示的UIView，设置圆角，边界，阴影
        let rect2 = CGRect(x: 10, y:75, width: 356, height: 120)
        let view2 = UIView(frame:rect2)
        view2.backgroundColor = UIColor.white
        view2.layer.shadowColor = UIColor.black.cgColor
        view2.layer.shadowOffset = CGSize(width: 10.0, height: 10.0)
        view2.layer.shadowOpacity = 0.45
        view2.layer.shadowRadius = 5.0
        view2.layer.cornerRadius = view2.frame.size.width/60;
        //view2.layer.masksToBounds = true;
        self.view.addSubview(view2)
        
        //显示当前分贝的文本框，并设置文本的字体和格式
        let rect3 = CGRect(x: 120, y: 70, width: 250, height: 80)
        lab = UILabel(frame: rect3)
        let font_k = UIFont(name: "Arial", size:15)
        lab.font = font_k
        lab.textAlignment = NSTextAlignment.left
        lab.textColor = UIColor.brown
        self.view.addSubview(lab)
        
        //显示当前位置的文本框，并设置文本的字体和格
        let rect5 = CGRect(x: 60, y: 120, width: 300, height: 80)
        lab1 = UILabel(frame: rect5)
        lab1.numberOfLines=0
        let font_k1 = UIFont(name: "Arial", size:15)
        lab1.font = font_k1
        lab1.textAlignment = NSTextAlignment.left
        lab1.textColor = UIColor.brown
        self.view.addSubview(lab1)
        
        
        //一个包含当前时间和上次上传时间显示的UIView，设置圆角，边界，阴影
        let rect4 = CGRect(x: 10, y:220, width: 356, height: 100)
        let view4 = UIView(frame:rect4)
        view4.backgroundColor = UIColor.white
        view4.layer.shadowColor = UIColor.black.cgColor
        view4.layer.shadowOffset = CGSize(width: 10.0, height: 10.0)
        view4.layer.shadowOpacity = 0.45
        view4.layer.shadowRadius = 5.0
        view4.layer.cornerRadius = view4.frame.size.width/60;
        self.view.addSubview(view4)
        
        
        //显示当前时间的文本框，并设置文本的字体和格式
        let rect6 = CGRect(x: 85, y: 220, width: 250, height: 80)
        lab2 = UILabel(frame: rect6)
  //      lab2.text = getTime()
        let font_k2 = UIFont(name: "Arial", size:15)
        lab2.font = font_k2
        lab2.textAlignment = NSTextAlignment.left
        lab2.textColor = UIColor.brown
        self.view.addSubview(lab2)
        
        //显示上次上传时间的文本框，并设置文本的字体和格式
        let rect7 = CGRect(x: 55, y: 255, width: 250, height: 80)
        lab3 = UILabel(frame: rect7)
        let font_k3 = UIFont(name: "Arial", size:15)
        lab3.font = font_k3
        lab3.textAlignment = NSTextAlignment.left
        lab3.textColor = UIColor.brown
        self.view.addSubview(lab3)
        
        //“点击上传”按钮
        let SendDataButton:UIButton=UIButton(frame:CGRect(x:90,y:340,width:200,height:40))
        SendDataButton.backgroundColor=UIColor.red
        SendDataButton.setTitle("点击上传", for:.normal)
        SendDataButton.titleLabel?.font=UIFont.systemFont(ofSize: 20)
        SendDataButton.addTarget(self, action: #selector(SendDataButtonAction), for:.touchUpInside)
        self.view.addSubview(SendDataButton)
        
        //用来后续显示噪声折线图的UIView，设置圆角，边界，阴影（里面的内容还未实现）
        let rect8 = CGRect(x: 10, y:400, width: 356, height: 200)
        let view5 = UIView(frame:rect8)
        view5.backgroundColor = UIColor.white
        view5.layer.shadowColor = UIColor.black.cgColor
        view5.layer.shadowOffset = CGSize(width: 10.0, height: 10.0)
        view5.layer.shadowOpacity = 0.45
        view5.layer.shadowRadius = 5.0
        view5.layer.cornerRadius = view5.frame.size.width/60;
        self.view.addSubview(view5)
        
        //导航栏
        let navigationBar=UINavigationBar(frame:CGRect(x:0,y:20,width:self.view.frame.size.width,height:45))
        navigationBar.backgroundColor=UIColor.white
        let titleLabel=UILabel(frame:CGRect(x:0,y:0,width:50,height:100))
        titleLabel.text="智能手机噪声收集系统"
        titleLabel.textColor=UIColor.black
        let navigationItem=UINavigationItem()
        navigationItem.titleView=titleLabel
//        let leftButton = UIBarButtonItem(title: "leftButton", style: .plain, target: self, action: nil)
   //     leftButton.tintColor = UIColor.red
        var img=UIImage(named:"menu")
        img=img?.withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        let rightButton = UIBarButtonItem(image: img, style: .plain, target: self, action: #selector(MenuButtonPressed) )

        navigationItem.setRightBarButton(rightButton, animated: false)
        navigationItem.setHidesBackButton(true, animated: false)
        navigationBar.pushItem(navigationItem, animated: false)
        self.view.addSubview(navigationBar)
        let VC = ViewController()
        present(VC,animated: true, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //点击上传按钮
    @objc func SendDataButtonAction(){
        var date = Date()
        var timeFormatter = DateFormatter()
        //日期显示格式，可按自己需求显示
        timeFormatter.dateFormat = "yyy-MM-dd HH:mm:ss"
        var strNowTime1 = timeFormatter.string(from: date) as String
        uploadtime=strNowTime1
        lab3.text="上次上传时间 "+strNowTime1

    }
    
    //获取当前时间
    @objc func GetCurrentTime(){
        var date = Date()
        var timeFormatter = DateFormatter()
        //日期显示格式，可按自己需求显示
        timeFormatter.dateFormat = "yyy-MM-dd HH:mm:ss"
        var strNowTime = timeFormatter.string(from: date) as String
        currenttime=strNowTime
        lab2.text="当前时间 "+(strNowTime)
    }
    
    //获取地理位置经纬度
    func GetLocation(){
        locationManager.delegate = self
        //设置定位精度
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        //更新距离
        locationManager.distanceFilter = 0.01
        locationManager.requestWhenInUseAuthorization()
        if (CLLocationManager.locationServicesEnabled())
        {
            //允许使用定位服务的话，开启定位服务更新
            locationManager.startUpdatingLocation()
            print("定位开始")
            
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        //获取最新的坐标
        currentLocation = locations.last!
        lab1.text = "当前位置: 经度\(currentLocation.coordinate.longitude)\n                纬度\(currentLocation.coordinate.latitude)"
        longitude=String(currentLocation.coordinate.longitude)
        latitude=String(currentLocation.coordinate.longitude)
        print(currentLocation.coordinate.longitude)
        //获取纬度
//        label2.text = "纬度：\(currentLocation.coordinate.latitude)"
        print(currentLocation.coordinate.latitude)
        //获取海拔
 //       label3.text = "海拔：\(currentLocation.altitude)"
    }
    
    func ClickMenu(sender: AnyObject){
        print(sender)
    }
    
    //菜单按钮
    @objc func MenuButtonPressed(){
        let menuArray = [KxMenuItem.init("使用说明", image: UIImage(named: "使用说明"), target: self, action: #selector(InstructionButtonPressed)),
        KxMenuItem.init("清空数据", image: UIImage(named: "清空数据"), target: self, action: #selector(DeleteDataButtonPressed)),
        KxMenuItem.init("刷新GPS", image: UIImage(named: "刷新GPS"), target: self, action:#selector(RefreshGpsButtonPressed))]
        KxMenu.setTitleFont(UIFont(name: "HelveticaNeue", size: 15))
        if flag==0{
            menuArray[0]?.action=nil
        }
        let a = CGRect(x: 300, y: 70, width: 0, height: 0)
        
        let options = OptionalConfiguration(arrowSize: 9,  //指示箭头大小
            marginXSpacing: 7,  //MenuItem左右边距
            marginYSpacing: 9,  //MenuItem上下边距
            intervalSpacing: 25,  //MenuItemImage与MenuItemTitle的间距
            menuCornerRadius: 6.5,  //菜单圆角半径
            maskToBackground: true,  //是否添加覆盖在原View上的半透明遮罩
            shadowOfMenu: false,  //是否添加菜单阴影
            hasSeperatorLine: true,  //是否设置分割线
            seperatorLineHasInsets: false,  //是否在分割线两侧留下Insets
            textColor: Color(R: 0, G: 0, B: 0),  //menuItem字体颜色
            menuBackgroundColor: Color(R: 1, G: 1, B: 1)  //菜单的底色
        )
            KxMenu.show(in: self.view, from: a, menuItems: menuArray, withOptions: options)
        
    }
    
    //点击使用说明按钮
    @objc func InstructionButtonPressed(){
        //print(1)
       // let rect9 = CGRect(x: 10, y: 80, width: 355, height: 530)
        flag=0
        view6 = UIView(frame:rect9)
        view6.backgroundColor = UIColor.white
        view6.alpha=0.9
        view6.layer.shadowColor = UIColor.black.cgColor
        view6.layer.shadowOffset = CGSize(width: 10.0, height: 10.0)
        view6.layer.shadowOpacity = 0.45
        view6.layer.shadowRadius = 5.0
        view6.layer.cornerRadius = view6.frame.size.width/60;
        self.view.addSubview(view6)
        
        let rect10 = CGRect(x: 140, y: 10, width: 300, height: 30)
        let lab4 = UILabel(frame: rect10)
        lab4.text = "使用说明"
        let font_k4 = UIFont(name: "Arial", size:20)
        lab4.font = font_k4
        lab4.textAlignment = NSTextAlignment.left
        lab4.textColor = UIColor.black
        view6.addSubview(lab4)
        
        let rect11 = CGRect(x: 50, y: 35, width: 300, height: 90)
        let lab5 = UILabel(frame: rect11)
        lab5.numberOfLines=0
        lab5.text = "(a)用户通过手机号和密码登录,若为初\n次使用则需要通过手机号注册,若用户\n忘记密码,则点击忘记密码即可修改密码"
        let font_k5 = UIFont(name: "Arial", size:15)
        lab5.font = font_k5
        lab5.textAlignment = NSTextAlignment.left
        lab5.textColor = UIColor.black
        view6.addSubview(lab5)
        
        let rect12 = CGRect(x: 50, y: 110, width: 300, height: 90)
        let lab6 = UILabel(frame: rect12)
        lab6.numberOfLines=0
        lab6.text = "(b)登录后会在首页显示当前分贝,当前\n位置,当前时间,上次上传时间。中间的\n点击上传按钮点击后可上传测得的噪\n声分贝数据，下部分显示噪声折线图"
        let font_k6 = UIFont(name: "Arial", size:15)
        lab6.font = font_k6
        lab6.textAlignment = NSTextAlignment.left
        lab6.textColor = UIColor.black
        view6.addSubview(lab6)
        
        let rect13 = CGRect(x: 50, y: 185, width: 300, height: 90)
        let lab7 = UILabel(frame: rect13)
        lab7.numberOfLines=0
        lab7.text = "(c)点击底部导航栏中的查询会进入查\n询界面,选择您要查询的时间后点击查\n询按钮即可查看噪声地图"
        let font_k7 = UIFont(name: "Arial", size:15)
        lab7.font = font_k7
        lab7.textAlignment = NSTextAlignment.left
        lab7.textColor = UIColor.black
        view6.addSubview(lab7)
        
        let rect14 = CGRect(x: 50, y: 270, width: 300, height: 90)
        let lab8 = UILabel(frame: rect14)
        lab8.numberOfLines=0
        lab8.text = "(d)点击底部导航栏我的即可进入个人主\n页,个人主页会显示您的手机号和积分等,\n同时还有上传记录,我的客服,我的信息\n,分享等几个功能选项"
        let font_k8 = UIFont(name: "Arial", size:15)
        lab8.font = font_k8
        lab8.textAlignment = NSTextAlignment.left
        lab8.textColor = UIColor.black
        view6.addSubview(lab8)
        
        let rect15 = CGRect(x: 50, y: 355, width: 300, height: 120)
        let lab9 = UILabel(frame: rect15)
        lab9.numberOfLines=0
        lab9.text = "(e)点击上传记录便会显示用户之前上传\n到服务器的上传时间上传地点和噪声分\n贝值等数据，点击设置后您可以选择注\n销登录或者切换账户，点击我的客服后\n您可以给我们的应用进行反馈留言，点\n击分享则会生成二维码用于分享本软件"
        let font_k9 = UIFont(name: "Arial", size:15)
        lab9.font = font_k9
        lab9.textAlignment = NSTextAlignment.left
        lab9.textColor = UIColor.black
        view6.addSubview(lab9)
        
        var img3=UIImage(named:"close_1")
        img3=img3?.withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        let closeButton = UIButton(type: .custom)
        closeButton.frame=CGRect(x:315,y:0,width:50,height:30)
        closeButton.setImage(img3, for: .normal)
        closeButton.addTarget(self, action: #selector(CloseButtonPressed), for: .touchUpInside)
        view6.addSubview(closeButton)
    }
    
    //点击清空数据按钮
    @objc func DeleteDataButtonPressed(){
        print(2)
    }
    
    //点击刷新GPS按钮
    @objc func RefreshGpsButtonPressed(){
        GetLocation()
    }
    
    //点击使用说明视图中的x
    @objc func CloseButtonPressed(){
        view6.isHidden=true
        flag=1
    }
    
    //开始录音
    func BeginRecord() {
        let session = AVAudioSession.sharedInstance()
        //设置session类型
        do {
            try session.setCategory(AVAudioSessionCategoryPlayAndRecord)
        } catch let err{
            print("设置类型失败:\(err.localizedDescription)")
        }
        //设置session动作
        
        do {
            try session.setActive(true)
        } catch let err {
            print("初始化动作失败:\(err.localizedDescription)")
        }
        
        //录音设置，注意，后面需要转换成NSNumber，如果不转换，你会发现，无法录制音频文件，我猜测是因为底层还是用OC写的原因
        let recordSetting: [String: Any] = [AVSampleRateKey: NSNumber(value: 16000),//采样率
            AVFormatIDKey: NSNumber(value: kAudioFormatLinearPCM),//音频格式
            AVLinearPCMBitDepthKey: NSNumber(value: 16),//采样位数
            AVNumberOfChannelsKey: NSNumber(value: 1),//通道数
            AVEncoderAudioQualityKey: NSNumber(value: AVAudioQuality.min.rawValue)//录音质量
        ];
        //开始录音
        
        do {
            let url = URL(fileURLWithPath: file_path!)
            recorder = try AVAudioRecorder(url: url, settings: recordSetting)
            recorder!.prepareToRecord()
            recorder!.record()
            recorder!.isMeteringEnabled=true
            print("开始录音")
        } catch let err {
            print("录音失败:\(err.localizedDescription)")
        }
        
    }
    
    //获取分贝，分段处理
    @objc func GetDecibels() //-> Float
    {
        recorder!.updateMeters()
        var db1=recorder!.averagePower(forChannel: 0)
        db1=db1+110
        var dB:Float
        if (db1 < 0.0) {
            dB = 0;
        } else if (db1 < 40.0) {
            dB = (Float(db1 * 0.875));
        } else if (db1 < 100.0) {
            dB = (Float(db1 - 15));
        } else if (db1 < 110.0) {
            dB = (Float(db1 * 2.5 - 165));
        } else {
            dB = 110;
        }
        dBvag=String(dB)
        lab.text = "当前分贝:\(String(dB))db"
    }
    
    func InsertIntoDict(){
//        dict["size"] = String(num)
        dict["longitude\(num)"] = longitude
        dict["latitude\(num)"] = latitude
        dict["currenttime\(num)"] = currenttime
///        dict["uploadtime\(num)"] = uploadtime
        dict["dBavg\(num)"] = dBvag
        num=num+1
    }
    
    @objc func getData(){
       GetCurrentTime()
       GetLocation()
       GetDecibels()
       InsertIntoDict()
        if (num==11){
            dict["uploadtime"] = dict["currenttime10"]
            dict["IMEI"] = "123456789"
            dict["phonemodel"] = "iphone"
            dict["protocal"] = myprotocol
            dict["size"] = "10"
            print(dict)
            StartRequest()
            num=1
            dict=[:]
        }
        print(dict)
    }
   func StartRequest(){
/*    Alamofire.request(url, method: .post, parameters: dict, encoding: JSONEncoding.default,headers:nil).responseJSON{
        response in
        if response.result.isSuccess{
            print("上传成功")
        }
        else
        {
            print("上传失败")
        }
    }
 */
    Alamofire.request(url, method: .post, parameters: dict,headers:nil).responseJSON{
        response in
        if response.result.isSuccess{
            print("上传成功")
        }
        else
        {
            print("上传失败")
        }
    }
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
//    let dict1: [String: String] = ["dBavg": "55" , "longitude": "27" , "latitude": "56"]

}
}
