//
//  settingViewController.swift
//  noise_system
//
//  Created by JamesRuio on 2018/11/29.
//  Copyright © 2018年 JamesRuio. All rights reserved.
//

import UIKit

class settingViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        //设置界面的导航栏设置
        let navigationBar=UINavigationBar(frame:CGRect(x:0,y:20,width:375,height:45))
        navigationBar.backgroundColor=UIColor.white
        let titleLabel=UILabel(frame:CGRect(x:0,y:0,width:50,height:100))
        titleLabel.text="设置"
        titleLabel.textColor=UIColor.black
        let navigationItem=UINavigationItem()
        navigationItem.titleView=titleLabel
        var img=UIImage(named:"back")
        img=img?.withRenderingMode(UIImageRenderingMode.alwaysOriginal)
        let leftButton = UIBarButtonItem(image:img, style: .plain, target: self, action: #selector(backButtonPressed) )
        //        rightButton.tintColor = UIColor.red
        //        navigationItem.setLeftBarButton(leftButton, animated: false)
        navigationItem.setLeftBarButton(leftButton, animated: false)
        navigationItem.backBarButtonItem=leftButton
        navigationItem.setHidesBackButton(true, animated: false)
        navigationBar.pushItem(navigationItem, animated: false)
        self.view.addSubview(navigationBar)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //点击左上角返回按钮
    @objc func backButtonPressed(_ button:UIButton){
        self.dismiss(animated: true, completion: nil)
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
