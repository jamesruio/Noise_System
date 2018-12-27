//
//  ViewController.swift
//  noise_system
//
//  Created by JamesRuio on 2018/11/22.
//  Copyright © 2018年 JamesRuio. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let loginButton:UIButton=UIButton(frame:CGRect(x:90,y:340,width:200,height:40))
        loginButton.backgroundColor=UIColor.red
        loginButton.addTarget(self, action: #selector(loginButtonAction), for:.touchUpInside)
        self.view.addSubview(loginButton)
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func loginButtonAction(){
    //    let SecondViewControllerVC = SecondViewController()
        //let VC=tabViewController()
        //present(VC,animated: true, completion: nil)
        //self.performSegue(withIdentifier: "showTab",sender:a)
    }


}

