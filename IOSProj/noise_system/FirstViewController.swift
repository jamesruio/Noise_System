//
//  FirstViewController.swift
//  noise_system
//
//  Created by JamesRuio on 2018/11/22.
//  Copyright © 2018年 JamesRuio. All rights reserved.
//

import UIKit
import MapKit
class FirstViewController: UIViewController,UITextFieldDelegate
{
    var datePicker = UIDatePicker()    //时间选择器
    let dformatter = DateFormatter()   //显示格式
    let textField = UITextField(frame: CGRect(x:60,y:100,width:250,height:40)) //显示选定日期的文本框
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title="查询"
        self.tabBarItem.image=UIImage(named:"search")?.withRenderingMode(.alwaysOriginal)
        self.view.backgroundColor=UIColor.white
        // Do any additional setup after loading the view.
        
        //导航栏
        let navigationBar=UINavigationBar(frame:CGRect(x:0,y:20,width:self.view.frame.size.width,height:45))
        navigationBar.backgroundColor=UIColor.white
        let titleLabel=UILabel(frame:CGRect(x:0,y:0,width:50,height:100))
        titleLabel.text="智能手机噪声收集系统"
        titleLabel.textColor=UIColor.black
        let navigationItem=UINavigationItem()
        navigationItem.titleView=titleLabel
        navigationBar.pushItem(navigationItem, animated: false)
        self.view.addSubview(navigationBar)
        
       //日期选择的文本框设置
       textField.borderStyle=UITextBorderStyle.roundedRect
       textField.placeholder = "请点击选择日期"
//       textField.addTarget(self, action: #selector(textFieldPressed), for: UIControlEvents.editingChanged)
       textField.layer.borderColor=UIColor.darkGray.cgColor
        textField.textAlignment = .center
        

        // 设置日期选择器样式，当前设为同时显示日期和时间
        datePicker.locale = NSLocale(localeIdentifier: "zh_CN") as Locale as Locale
        datePicker.timeZone = NSTimeZone.system
        textField.delegate=self
        self.view.addSubview(textField)
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
/*    @objc func GetDate(datePicker: UIDatePicker) {
        let formatter = DateFormatter()
        let date = datePicker.date
        formatter.dateFormat = "yyyy年MM月dd日 HH:mm"
        let dateStr = formatter.string(from: date)
        self.textField.text = dateStr
    }
*/
    func Alert() {
        datePicker.frame=CGRect(x:20, y:40, width:320, height:200)
        //设置最大日期为当前日期
        datePicker.maximumDate = NSDate() as Date
        datePicker.datePickerMode = UIDatePickerMode.date
        // 响应事件（只要滚轮变化就会触发）
//        datePicker.addTarget(self, action:#selector(myAction), for: UIControlEvents.valueChanged)
        
        // 为日期格式器设置格式字符串
        dformatter.dateFormat = "yyyy-MM-dd"
        //  初始化alertController
        let alertController = UIAlertController(title: "请选择\n\n\n\n\n\n\n\n\n\n\n\n", message: nil, preferredStyle:  .actionSheet)
        let cancleAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
        let doneAction = UIAlertAction(title: "确定", style: .default) { (action: UIAlertAction) in
            // 使用日期格式器格式化日期、时间
            let date = self.datePicker.date
            let selectedDate = self.dformatter.string(from: date)
            self.textField.text = selectedDate
            self.view.endEditing(true)
        }
        alertController.view.addSubview(datePicker)
        alertController.addAction(doneAction)
        alertController.addAction(cancleAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    // 防止点击文本框弹出键盘
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        Alert()
        return false
    }
    
}




