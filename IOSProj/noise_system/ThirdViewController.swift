//
//  ThirdViewController.swift
//  noise_system
//
//  Created by JamesRuio on 2018/11/22.
//  Copyright © 2018年 JamesRuio. All rights reserved.
//

import UIKit
import Foundation

class ThirdViewController: UIViewController{
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title="我的"
        self.tabBarItem.image=UIImage(named:"me")?.withRenderingMode(.alwaysOriginal)
        self.view.backgroundColor=UIColor.white
        // Do any additional setup after loading the view.
        
        //一个包含屏幕上半部分的UIView，基本信息的显示
        let rect1 = CGRect(x:0, y:50, width: 400, height: 250)
        let view1 = UIView(frame:rect1)
        view1.backgroundColor = UIColor.lightGray
        self.view.addSubview(view1)
        
        let rect2 = CGRect(x: 10, y:75, width: 356, height: 200)
        let view2 = UIView(frame:rect2)
        view2.backgroundColor = UIColor.white
        view2.layer.shadowColor = UIColor.black.cgColor
        view2.layer.shadowOffset = CGSize(width: 10.0, height: 10.0)
        view2.layer.shadowOpacity = 0.45
        view2.layer.shadowRadius = 5.0
        
        view2.layer.cornerRadius = view2.frame.size.width/60;
        //view2.layer.masksToBounds = true;
        self.view.addSubview(view2)
        
        //word
        let rect3 = CGRect(x: 25, y: 110, width: 250, height: 80)
        let lab = UILabel(frame: rect3)
        lab.text = "15332342922"
        let font_k = UIFont(name: "Arial", size:30)
        lab.font = font_k
        
        lab.textAlignment = NSTextAlignment.left
        lab.textColor = UIColor.brown
        self.view.addSubview(lab)
        
        let rect4 = CGRect(x: 25, y: 180, width: 200, height: 50)
        let view4 = UIView(frame:rect4)
        view4.backgroundColor = UIColor.lightGray
        self.view.addSubview(view4)
        
        let lab1 = UILabel(frame: rect4)
        lab1.text = "我的积分：111"
        let font_k1 = UIFont(name: "Arial", size:20)
        lab1.font = font_k1
        
        lab1.textAlignment = NSTextAlignment.center
        lab1.textColor = UIColor.black
        self.view.addSubview(lab1)
        
        //image
        let rect5 = CGRect(x: 270, y: 140, width: 70, height: 70)
        let view5 = UIView(frame:rect5)
        view5.backgroundColor = UIColor.lightGray
        self.view.addSubview(view5)
        view5.layer.cornerRadius = view5.frame.size.width/2;
        view5.layer.masksToBounds = true;
        
        let image_user = UIImageView()
        image_user.image = UIImage(named:"usr")
        image_user.frame = CGRect(x: 270, y: 140, width: 70, height: 70)
        image_user.layer.cornerRadius = image_user.frame.size.width/2;
        image_user.layer.masksToBounds = true;
        self.view.addSubview(image_user)

        //下半部分
        //第一个外围view设置边框
        let rect_t1 = CGRect(x: 0,y: 350,width: 375,height: 30)
        let view_txt1 = UIView(frame:rect_t1)
        view_txt1.addBorder(side:.bottom, thickness: 0.5, color: UIColor.lightGray)
        view_txt1.isUserInteractionEnabled=true
        let tapGesture1=UITapGestureRecognizer(target: self, action: #selector(TapGesture1Action))
        tapGesture1.numberOfTapsRequired = 1
        view_txt1.addGestureRecognizer(tapGesture1)
        self.view.addSubview(view_txt1)
        //设置文字
        let rect_txt1 = CGRect(x: 50,y: 350,width: 228,height: 20)
        let label1 = UILabel(frame: rect_txt1)
        label1.text = "上传记录"
        
        let font = UIFont(name: "Arial", size:18)
        label1.font = font
        
        label1.textAlignment = NSTextAlignment.left
        label1.textColor = UIColor.brown
        self.view.addSubview(label1)
        //设置两个图片
        let image11 = UIImageView()
        image11.image = UIImage(named:"arrow")
        image11.frame = CGRect(x: 350, y: 350, width: 12, height: 21)
        self.view.addSubview(image11)
        
        let image12 = UIImageView()
        image12.image = UIImage(named:"upload")
        image12.frame = CGRect(x: 10, y: 345, width: 30, height: 27)
        self.view.addSubview(image12)
    
        //第二个外围view设置边框
        let rect_t2 = CGRect(x: 0,y: 390,width: 375,height: 30)
        let view_txt2 = UIView(frame:rect_t2)
        view_txt2.addBorder(side:.bottom, thickness: 0.5, color: UIColor.lightGray)
        view_txt2.isUserInteractionEnabled=true
        let tapGesture2=UITapGestureRecognizer(target: self, action: #selector(TapGesture2Action))
        tapGesture2.numberOfTapsRequired = 1
        view_txt2.addGestureRecognizer(tapGesture2)
        self.view.addSubview(view_txt2)
        //设置文字
        let rect_txt2 = CGRect(x: 50,y: 390,width: 228,height: 20)
        let label2 = UILabel(frame: rect_txt2)
        label2.text = "我的客服"
        
        let font2 = UIFont(name: "Arial", size:18)
        label2.font = font2
        
        label2.textAlignment = NSTextAlignment.left
        label2.textColor = UIColor.brown
        self.view.addSubview(label2)
        //设置两个图片
        let image21 = UIImageView()
        image21.image = UIImage(named:"arrow")
        image21.frame = CGRect(x: 350, y: 390, width: 12, height: 21)
        self.view.addSubview(image21)
        
        let image22 = UIImageView()
        image22.image = UIImage(named:"waiter")
        image22.frame = CGRect(x: 10, y: 385, width: 30, height: 27)
        self.view.addSubview(image22)
        
        //第三个外围view设置边框
        let rect_t3 = CGRect(x: 0,y: 430,width: 375,height: 30)
        let view_txt3 = UIView(frame:rect_t3)
        view_txt3.addBorder(side:.bottom, thickness: 0.5, color: UIColor.lightGray)
        view_txt3.isUserInteractionEnabled=true
        let tapGesture3=UITapGestureRecognizer(target: self, action: #selector(TapGesture3Action))
        tapGesture3.numberOfTapsRequired = 1
        view_txt3.addGestureRecognizer(tapGesture3)
        self.view.addSubview(view_txt3)
        //设置文字
        let rect_txt3 = CGRect(x: 50,y: 430,width: 228,height: 20)
        let label3 = UILabel(frame: rect_txt3)
        label3.text = "我的信息"
        
        let font3 = UIFont(name: "Arial", size:18)
        label3.font = font3
        
        label3.textAlignment = NSTextAlignment.left
        label3.textColor = UIColor.brown
        self.view.addSubview(label3)
        //设置两个图片
        let image31 = UIImageView()
        image31.image = UIImage(named:"arrow")
        image31.frame = CGRect(x: 350, y: 430, width: 12, height: 21)
        self.view.addSubview(image31)
        
        let image32 = UIImageView()
        image32.image = UIImage(named:"message")
        image32.frame = CGRect(x: 10, y: 425, width: 30, height: 27)
        self.view.addSubview(image32)
        
        //第四个外围view设置边框
        let rect_t4 = CGRect(x: 0,y: 470,width: 375,height: 30)
        let view_txt4 = UIView(frame:rect_t4)
        view_txt4.addBorder(side:.bottom, thickness: 0.5, color: UIColor.lightGray)
        view_txt4.isUserInteractionEnabled=true
        let tapGesture4=UITapGestureRecognizer(target: self, action: #selector(TapGesture4Action))
        tapGesture4.numberOfTapsRequired = 1
        view_txt4.addGestureRecognizer(tapGesture4)
        self.view.addSubview(view_txt4)
        //设置文字
        let rect_txt4 = CGRect(x: 50,y: 470,width: 228,height: 20)
        let label4 = UILabel(frame: rect_txt4)
        label4.text = "分享"
        
        let font4 = UIFont(name: "Arial", size:18)
        label4.font = font4
        
        label4.textAlignment = NSTextAlignment.left
        label4.textColor = UIColor.brown
        self.view.addSubview(label4)
        //设置两个图片
        let image41 = UIImageView()
        image41.image = UIImage(named:"arrow")
        image41.frame = CGRect(x: 350, y: 470, width: 12, height: 21)
        self.view.addSubview(image41)
        
        let image42 = UIImageView()
        image42.image = UIImage(named:"share")
        image42.frame = CGRect(x: 10, y: 465, width: 30, height: 27)
        self.view.addSubview(image42)
        
        //第五个外围view设置边框
        let rect_t5 = CGRect(x: 0,y: 510,width: 375,height: 30)
        let view_txt5 = UIView(frame:rect_t5)
        view_txt5.addBorder(side:.bottom, thickness: 0.5, color: UIColor.lightGray)
        view_txt5.isUserInteractionEnabled=true
        let tapGesture5=UITapGestureRecognizer(target: self, action: #selector(TapGesture5Action))
        tapGesture5.numberOfTapsRequired = 1
        view_txt5.addGestureRecognizer(tapGesture5)
        self.view.addSubview(view_txt5)
        //设置文字
        let rect_txt5 = CGRect(x: 50,y: 510,width: 228,height: 20)
        let label5 = UILabel(frame: rect_txt5)
        label5.text = "用户设置"
        
        let font5 = UIFont(name: "Arial", size:18)
        label5.font = font5
        
        label5.textAlignment = NSTextAlignment.left
        label5.textColor = UIColor.brown
        self.view.addSubview(label5)
        //设置两个图片
        let image51 = UIImageView()
        image51.image = UIImage(named:"arrow")
        image51.frame = CGRect(x: 350, y: 510, width: 12, height: 21)
        self.view.addSubview(image51)
        
        let image52 = UIImageView()
        image52.image = UIImage(named:"settings")
        image52.frame = CGRect(x: 10, y: 505, width: 30, height: 27)
        self.view.addSubview(image52)
        
        //第六个外围view设置边框
        let rect_t6 = CGRect(x: 0,y: 550,width: 375,height: 30)
        let view_txt6 = UIView(frame:rect_t6)
//        view_txt6.addBorder(side:.bottom, thickness: 0.5, color: UIColor.lightGray)
        self.view.addSubview(view_txt6)
        
        let navigationBar=UINavigationBar(frame:CGRect(x:0,y:20,width:self.view.frame.size.width,height:45))
        navigationBar.backgroundColor=UIColor.white
        let titleLabel=UILabel(frame:CGRect(x:0,y:0,width:50,height:100))
        titleLabel.text="智能手机噪声收集系统"
        titleLabel.textColor=UIColor.black
        let navigationItem=UINavigationItem()
        navigationItem.titleView=titleLabel
        navigationBar.pushItem(navigationItem, animated: false)
        self.view.addSubview(navigationBar)
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    //跳转到上传记录界面
    @objc func TapGesture1Action(){
        let firstVC = FirstVC(nibName:"FirstVC",bundle:nil)
        present(firstVC,animated: true, completion: nil)
    }
    
    //跳转到我的客服界面
    @objc func TapGesture2Action(){
        let customerServiceVC = customerServiceViewController(nibName:"customerServiceViewController",bundle:nil)
        present(customerServiceVC,animated: true, completion: nil)
    }
    
    //跳转到我的信息界面
    @objc func TapGesture3Action(){
        let myMessageVC = myMessageViewController(nibName:"myMessageViewController",bundle:nil)
        present(myMessageVC,animated: true, completion: nil)
    }
    
    //跳转到分享界面
    @objc func TapGesture4Action(){
        let shareVC = shareViewController(nibName:"shareViewController",bundle:nil)
        present(shareVC,animated: true, completion: nil)
    }
    
    //跳转到用户设置界面
    @objc func TapGesture5Action(){
        let settingVC = settingViewController(nibName:"settingViewController",bundle:nil)
        present(settingVC,animated: true, completion: nil)
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
