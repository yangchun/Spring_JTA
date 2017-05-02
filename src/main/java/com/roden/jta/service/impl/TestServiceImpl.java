package com.roden.jta.service.impl;


import javax.annotation.Resource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.roden.jta.dao.TestMasterDao;
import com.roden.jta.dao.TestSlaveDao;
import com.roden.jta.service.TestService;
@Service
public class TestServiceImpl implements TestService {

	@Resource(name = "springTransactionManager")
    private JtaTransactionManager txManager;
    @Autowired
    private TestMasterDao testMasterDao;
    @Autowired
    private TestSlaveDao testSlaveDao;  

    @Resource(name = "transactionTemplate")
    private TransactionTemplate transactionTemplate;  
    //���ʽ
    @Override
    public String test() {
         UserTransaction userTx = txManager.getUserTransaction(); 
         try {               
             userTx.begin();     
             testMasterDao.master(); 
             testSlaveDao.slave();    
             int a=1/0;
             System.out.println(a);
             userTx.commit();
         } catch (Exception e) {
             System.out.println("�����쳣�����лع�" + e.getMessage());
             e.printStackTrace();
             try {
                 userTx.rollback();
             } catch (IllegalStateException e1) {
                System.out.println("IllegalStateException:" + e1.getMessage());
             } catch (SecurityException e1) {
                 System.out.println("SecurityException:" + e1.getMessage());
             } catch (SystemException e1) {
                 System.out.println("SystemException:" + e1.getMessage());
             }              
         }
        return null;
    }
    //����ʽ
    @Override
    @Transactional
    public String update(){
         testMasterDao.master();        
         testSlaveDao.slave();  
//         int n=1/0;
        return null;
    }
    //����ģ�巽ʽ
    @Override
     public void test3() {  

            transactionTemplate.execute(new TransactionCallbackWithoutResult(){  
                @Override  
                protected void doInTransactionWithoutResult(TransactionStatus status) {  
                    try {  
                         testMasterDao.master();        
                         testSlaveDao.slave();   
                         int a=1/0;
                        System.out.println(a);
                    } catch (Exception ex) {  
                        // ͨ������ TransactionStatus ����� setRollbackOnly() �������ع�����  
                        status.setRollbackOnly();  
                        ex.printStackTrace();  
                    }  
                }  
            });         


               /* 
                //�з���ֵ�Ļص�
                 Object obj=transactionTemplate.execute(new TransactionCallback(){
                    @Override
                    public Object doInTransaction(TransactionStatus status) {

                        return 1;
                    }  
                });  */
        }  




}
