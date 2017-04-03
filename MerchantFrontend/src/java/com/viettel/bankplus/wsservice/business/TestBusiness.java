/*
 * Copyright (C) 2014 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.bankplus.wsservice.business;

import com.viettel.rsa.Crypto;
import com.viettel.security.PassTranformer;
import com.viettel.ttbankplus.servicegw.hibernate.dao.entity.Warehouse;
import com.viettel.ttbankplus.servicegw.hibernate.processor.DBProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author cuongdv3@viettel.com.vn
 * @since Nov 17, 2014
 * @version 1.0
 */
public class TestBusiness {

    public static void main(String[] args) throws Exception {
//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsAjvNLjsWkDTLduD7RA/v0aEGNJibQdkza7k9Zw37BowdGowtcJKH/Ga8L1uLzPFFdIEv80xD0rTJuVXA9w2OQAC9sbiv2Y/7QNREw1DkxCnWKqqAOEXEspYONsBazmURbtfxVkHMYSbIZZURjVUSJDQdm5tRmSYc7SPVP9uXTjPxE8KQb4uZAsw1mByTOeRw17Mv6mh55SDfbzvja09aZpog1y4pUxQVQvtZ0hsNgFENHEEr26BfwJYk4IAetcQx0tUXn5o+EqC0XLYi2dzdAlXLgD8sckPnD/WKO7NhqG4xSp1zp1v8FaW7MmeJgyyWN25D09FgqJEHrcrYB1Q0QIDAQAB";
//        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCwCO80uOxaQNMt24PtED+/RoQY0mJtB2TNruT1nDfsGjB0ajC1wkof8ZrwvW4vM8UV0gS/zTEPStMm5VcD3DY5AAL2xuK/Zj/tA1ETDUOTEKdYqqoA4RcSylg42wFrOZRFu1/FWQcxhJshllRGNVRIkNB2bm1GZJhztI9U/25dOM/ETwpBvi5kCzDWYHJM55HDXsy/qaHnlIN9vO+NrT1pmmiDXLilTFBVC+1nSGw2AUQ0cQSvboF/AliTggB61xDHS1Refmj4SoLRctiLZ3N0CVcuAPyxyQ+cP9Yo7s2GobjFKnXOnW/wVpbsyZ4mDLJY3bkPT0WCokQetytgHVDRAgMBAAECggEAMH21jvtluBLlWVt/wtzYApOlLcU+cbQ4s2P7FCE9Ul+Ae4EI+/iSPKxsNuanpH8JSlJ5FnCqlADTm4JPbvPJR/hV2K6+pWTANUTVn37m32sV/hm9DNL0bSuqE7dYc7X/vxm3bwBwN9bEeQMVIeXs2rRxiD4xAdQSHsVdOlfGigz363UV/vLScSyPbprBzmdEbmLi5W+/gU8JsBwtXfbbm1i/1ApTdbsoK/qjzJpGnrArd7uaDQRIUIIdSQZhihcATxoOVBPN2ERT5kbBnLR5AG1ewbpeQozskeC6+edN53i8mbTM4K4btnvdKeCH/1TewE12qeBmpREH32L/F8SSNQKBgQDmnL2yyS3jvMT/4rQnr5yaYRZk2qNOCBSU6iwI0myWYEtUjZsCaGPjyDtz85O9AxPAnOOZoGc4r2PQEbo4SaaS/tFIWeREuNIrnnTgNBbnFAQKJQo8iYDnC1ZmulZbgUbxIn6z8DX+3eF0U5l9qmgbzBnsnJxflzx91mB9cMXjLwKBgQDDag9UJvjILdGFGK5tSIyfz2znAPcc4WDHCggU1ITaOZadrwW23G3vPD5crSZaEvfOjSSZZ+gq8y2EkBQ8LoQNGoiwc+q69PpGHgnIs8xnm0aOjCGljlJow4vKHw2isQOnbOD0QmQIZc1bWjBBnEnKFEjXG1/kd2uL/rOGvlML/wKBgGnM0U8b8zy/VxYaeRKYA93j2vVMRY3AMUkcojUt7PmUlhrlmI8jO2i+bWBGCKq3pbFBiT3rFFAyTzWfXHMEfzKDS5rI+uv4axvt56sQ2s0LJstMsQSTBRt63RnmNUZn6hco/z4oX21deFj5HbtEmdze56nhM/C7HtqQptYGDhOrAoGBAIMgw2J7uEtTe5ntYvaQTVTbsr8cRbYKgo9tatjXdsKdDDWJgbMgHWEAsvQreGXO/pyK17ldIUzY231t1TZBHHlSidCmKCgGRX8Rnw0foZUQe/shi151r1T/iuk5h0PZtpL9m8IsXsXH0lJvpZwRTO6eR/aYNvOvMurBIiTTyBRvAoGBAMXHTRCbcefD0qnZkkh6dYT0Bojep1kb6sxPcu2ZJqojprL/YUFp0/80swqesIlh4iNevmFs9gLcy6ZZRHfzADJR8tvi0TKaB7EKCjph55dTUX0/MALFLJTGa+gSxBeo149vhOaVhx/7K/NmN1+PoK0zSddiNv3kOKfuPKZxZScS";
//        String clearText = "{\"username\":\"CP_PINCODE\", \"password\":\"12345678\", \"amount\":\"100000\", \"order_id\":\"dvcvn1234567\"}";
//
//        String encrypted = Crypto.Encrypt(clearText, publicKey, false);
//        String signature = Crypto.Sign(clearText, privateKey, false);
//        System.out.println(encrypted);
//        System.out.println();
//        System.out.println(signature.replace("\r", "").replace("\n", ""));
//        RequestData reqData = new RequestData();
//        reqData.setCmd("CHECKOUT");
//        reqData.setEncrypted(encrypted);
//        reqData.setSignature(signature);
//        reqData.setIpAddress("192.168.152.18");
//        BusinessManager.getInstance().onRequest(reqData);
//        String res = BusinessManager.getInstance().getResponse(reqData.getUuid());
//        System.out.println(res);
        
//        DBProcessor dbProcessor = new DBProcessor();
//        List lst = dbProcessor.getWarehouceProcessor().getPincode(10000L, 2, "A", "ABCVN");
//        System.out.println(lst.size());
//        System.out.println(lst);
//        List<Warehouse> lst = new ArrayList<Warehouse>();
//        for(int i=0;i<5;i++){
//            Warehouse wh = new Warehouse();
//            wh.setSerial("SERI_" + i);
//            wh.setPincode("PINCODE_" + i);
//            lst.add(wh);
//        }
//        System.out.println(DataUtils.getJsonArray(lst));
        
//        String[] key = Crypto.GenRSAKey(2048);
//        System.out.println(key[0]);
//        System.out.println();
//        System.out.println(key[1]);
//        System.out.println();
//        System.out.println();
//        System.out.println(PassTranformer.encrypt(key[0]));
//        System.out.println();
//        System.out.println(PassTranformer.decrypt("9eff117baa3dd80e5924a4d0e93958f53c1662dd6c4616444dca9a8d0338af2fe5b7f2b203110bab5270af600cb86612a0717295c21021cd2c00ffc85f79f0a1a89843e31219c69698d18bc45751e482ac6c1b1cbd7d72b2edfc142d1347bf6c3600892c6d850b99296a19117069fe45746eea7dd6e8f0079a0d288a1346011b97a0c3103f23f2ac7fa1bb44f12e5ccf99fc640c3e03893e165873dae87d1cf3944787f5255142eaf527af5f72af35bd1c571c3a59c699104279ac05b136d4b4ed859e78cb5682e918d55d0e70670679fbb631a057e526bf81085070d61d923356df457852f1ed7e7ba560b529d2ae48153677792a886bd9f54b5aed288193f87382b0e8ba1f36679794c8146ef1b9bcf2e522e26aa491816b89da53e4f832fb73f1073ee149f23f73dfb9a390f765420c6784358b8c3e13df5eabff9ad2d1637c223f1a666cc74eaae8564193b8f0fa1354a9569d95496567518089c4f3d5e956133bdae267b38570655f28af50d9ed33e33d5e28afc6f3b519da13af2ea4c2c0f339905af60e6f4687ccba21e07165"));
//                                                  "9eff117baa3dd80e5924a4d0e93958f53c1662dd6c4616444dca9a8d0338af2fe5b7f2b203110bab5270af600cb86612a0717295c21021cd2c00ffc85f79f0a1a89843e31219c69698d18bc45751e482ac6c1b1cbd7d72b2edfc142d1347bf6c3600892c6d850b99296a19117069fe45746eea7dd6e8f0079a0d288a1346011b97a0c3103f23f2ac7fa1bb44f12e5ccf99fc640c3e03893e165873dae87d1cf3944787f5255142eaf527af5f72af35bd1c571c3a59c699104279ac05b136d4b4ed859e78cb5682e918d55d0e70670679fbb631a057e526bf81085070d61d923356df457852f1ed7e7ba560b529d2ae48153677792a886bd9f54b5aed288193f87382b0e8ba1f36679794c8146ef1b9bcf2e522e26aa491816b89da53e4f832fb73f1073ee149f23f73dfb9a390f765420c6784358b8c3e13df5eabff9ad2d1637c223f1a666cc74eaae8564193b8f0fa1354a9569d95496567518089c4f3d5e956133bdae267b38570655f28af50d9ed33e33d5e28afc6f3b519da13af2ea4c2c0f339905af60e6f4687ccba21e07165"
//                                                  "9eff117baa3dd80e5924a4d0e93958f53c1662dd6c4616444dca9a8d0338af2fe5b7f2b203110bab2ff2d1398895b61238b05c2144c10aa0c477903339c4d36ffda70f74ab957ae0fd324b50436ee8cc2538c6df54adf918acbe7b0755358fbe0c580ff499e0eb664f00715509f820a94d5ed28e43a9bb46554a97ae3661188a857b89bfc4149bfb01b78f405a33e004ba160548d40e5673a89f224310ed92f2a54fea522a77295d878abb37a3de5d5068b84b57642648fbfaea7c8b780923c8cad4c89cc7387fcd7685d0c7baab9b24f7e4992e17fe47f1b87326c034d47d741d64c6ab6222bbdeab9c652cca6879ec6f6701c1c08a946935d5c03878e7b57d1a0079cab034798908b2d83285e00f1a4f4094bb22199291a5be628a6cc1975de35acdc2e0ec8991cf13a496a7715055d83cdb30244a05990605ee929996c285a1be71923f247ce1907bf9d7d285bbea44e95ec7a393b81625578c223f4d2789ea07cf125d323ef4fb15484cecb2de529e6b4dc1cd5d93ee969d2c8cdf42811803e2be32209912dc4687ccba21e07165"
//        System.out.println(PassTranformer.decrypt("6e04823cf8cfe018dcf91c86943fc892eb174032a2c592080c2cbd41f2b6ab17a9fff2a734e61da196482b892429e42ac56373504b9ce471a542c44e0a4dca1c749c10a9fe02eca785dc27f5dc6d5944034d009b90da2c3ffba10256d41b622e7bda610fa5fee0e9494dccb37cc3ff498fefa72e2fe3dff23f440e6330acec56fc20e6dee81bcd314f184015d498296141d3e9d587e462ce0b3e58510df6bdfc2b10cc88bbe165926e47e2a76a89e87e758c194643de51728babe1b303f84a25b28af144c3b4378809496800cc1aea0f25c8a5ece8eebc05eb779ae7d0e1ed7c7d07efdd61205936e3dc4fc5d15ca7c1aa7c0fccb94e48429a11668e026ee9a38c55e2fb4013c1632d80a609a480a8c08f1cc7cae09143e00dd49dbffe6ff4527ab8a81651629aa6e73e29e9ae3a40fcf6eb01acceae13477a039238072115c6203eda014703f74dc9ef14c8c24d4b311abed7e39c46d0d3da5b7209081d28d35d9a67d521a8af70ecee3f536f38434613d5908fb2ba0ffebe1968280b8417650942ba8107eb144606dd4497355d49f71530af8c526d48b4eda398cea8a7b402d5dd387f0000baa5551f9901a69c383c702e2a364d8ba87676c880639b1af387369e91d31f19369c16ef47461bfd7e416c50cf671605c87cc715e91418466e9ed83377f274344e22a212b7da8704bacd3fb758a8b1a00a7a06aebf5f1603b9f199587fb8d0fed9d102f8092f362c7fc6291656180067a957f566726d22e317f7aab175d527f2e3fae9820cb1d777aa3fb3e8baf34fd0482e6f23fe6bdc6f79d5c2eb9e72166e2b44e478604d313d0a5fb1f25056a2a57c0741c364cc92b1692276c05b86651e83b40fe6dd07e2923590f479aebb9f93773525b5caccc64eb929c779a808851c933fb3fdaf6dad509ec63940dcf94b1a34758a863bf1c4e426eca91439fe8df90865dca5117fcf8bd196e1c29b3cc9965446b9d0cc8ed5d6a9b7ee8439620e5eae8bc0ad789b26c2081e400cd71861dc18ad0516aaba2810dbcfe7ee319a6f1b6b01faefa8e1b661d9776e054532a27c9613e993063c381d9344cda505bd402a9781485b9fb2d8e9b88661ed71100cfde38608b3692945cf4f62da1e5d4a1005a802a244a3dd80d580aa58b0ed030e8708cd9808ede7704323f730963ae79d0acab89ee70d78a02aee3810223e334c662fba30e7f27ce4865cd819e55c56619833164475478f9fc21e9179d6140822c33615634b7f49e68257d79746de71d661f83fc99c57c218d7c0b344f544e2ae30a74ad7067d595ef6819a4c4255f475063b312c95e14446c082f3ac5b8ee09164c553ad4410a2d42e470952663d39dbda233ea08d9eaac8e8eadbfface1c10ae0e8975f9af7c3e11d44eddd971c05dbce0ec178e43507aa8fa6375b36cb0bbc9dc9ffa24a20eb7bd2cd92b8a8e09cdd4971f760c77671e9876e6968e60a1d1d1129327131e0545eccbd6b17c09a2b7b4a61f04587258b3a358f020e007c4d3a780be804e3fa63eeb7ba7146ece8e7c445380f4b75af834d01334d5cb2a901b1d815a9e86cf9f0f6bab0506a30c8b17a7712471d5f3fa3ce99836a918dd938eaaecd811c3437b077381b015ab249c9c681a990c55e44c59ebf799a4f83c0046ad1921dd4b66e3d9878e3604584041408da88988e3bcd8e8b24b3de8a1f356c340d956dba9eb88b315c330c5bd28ae126a8cfad3bfe1856d278637099117dc52b2e83ad8de1305c4383fcf59090c47f5cceebf051074fe7089624c00a99f7054106a833cdaa5351940d615cfb24ad9e583d55fd3065744b0c1ea1a81291f1957cfd9322ed33c9fbfa15a6a6727abc40e383d4439c75e5b03f63d27a1bb14807dce595d9e42044d753a0a2ba7d2a9a3930dadb5b7fca1771fe82c0f9ebae334fb3770fac88bf1da6e5e870cb9db50e1e336c5ed863d94d9663bdfc4d34eea16ad487aaecf1fab1dcc724f903ee871826c273f713bf948b7656d3d18ef3f54bd6d590fbce243783f8734dce75005f9c44a155d49597740c1958dc697718ad13df0b2279d7b613b2156ad04e836e68ef128cd687bfefebfb6cfcdc678db6dd3d036e230e33695636c4b2968af8d42891871349df8d48050fdafd64b829e54eefd3f153e658e84462fe4126be006854576b388d49a7e5ba73742859cc37e6daf128a0fc03fc7d3945a1d2b6891a58edee1c4c7bac80665568adf6693ef96296195c940246cbca22aed8494b1ab3b78409acd6c1412109ceb2de35e5c47822ffc37f9aaee3ca4687ccba21e07165"));


        String pubKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCqmxYheEmNEJlR5vtVr9OMznperPcWNN23tfxDu99eVSx2UeTbWMTQ1wuM05JYp4Koo9UtIX3qXIu/a6fzvYFch2jmkNck4Ln7MSekHqz9MC/JggHVz+SUb1Cv2PEZbwNCRjG8HynzPQnmxwpZ2KsON3UC4mAYJEIDYrL5ME2ax+2MoxkqmyDOwNbI+bydlpeGF7lxVa763EcHi2sWZVrdtq+v5IsC7JpR5xtwlTaE7suO+h7Y6OA9SZbWEvRT7ryCfv8p+vhkoWbSG9vSBdbQnkAKiOAm8y8jmcg2VK+sy6pZBz333+G3WYQmL54xhgHIZYDtnsMx6fHrMbW724vXAgMBAAECggEAPI7mUSUgfok40yC5cFYEMsQMHdUps+E6Hn7jt6tVg2eaa7lwEgg1fKgIuolTNlLnhzL4dZeg2XLWTDFLobJP2+dY7hbVt4ppmed92bzlzM2w+MdEHYA49BN7QaAxHMUSFBFUkCFDw8qT3C4k13WYamh3CXoPgCJGqGr9eoSR13Gxag5nY5Z7Rw+QTaOdxTK9C4Htj5iF1kTnP/gfmlXgpnCKOohf03S3+euL2fzfM0HDofzSBX5aTvyPY10hkGTNwosniRQZ/gf07BPwP4+mmgaP1vePuBnOC81onmL1n0aiFKry07ex87gFriTj1Bftz5SxXb/FxD6Kfn5UrZcAYQKBgQDh0qv1O11epipGTyY6VucWXwjJ7pyXK14EVvQvp+pCr0PLChSozceqEGtaVhw1UTKl81f/9uzfgrO7LwOCWb/nY9ERRStTYJm1oyG85Cy2Q/8/ZWFStUdbVnKPqDzKXIQZDCL/zUyjG5HgeGDG9pJ9ZedR4Sj+05zH3SOFexGn8QKBgQDBZ3R0sofBPTwJWSHmsfPq3CweePYFTZY2ORZzNXXxyGxOF81TnKL1WtqxiXqnP8NsErhWvRfFBnEIUjK6DTeDcJwOkywHBXBvNeaEnfpWVRQ/O8UhfhyUMCgV7QM9cmQZPKMwb62l8ro7O4IHaJnaSsLuGwz0Hfy2aD1zjA14RwKBgHOjhhsWQ0HspUlLzq+hQMTb9YwyfE2ND5/5RyIAQp3TUkvu5ZVriwszTUX9QoSrB9yjODUDJVlvfMol0Q43F2ZOXwkQ+pa/qPayitxHm/3Z5CN6rtPDsL69Df0yka6uoZaR+Ho1j0EXJSRlgGliarF8NXBgdLXJd0eBnmcEFo6BAoGALhh+RSIYARKbMI8x6U4YmK2JRmoGGRV468uHckYXqlzek25Q3dDDdj8FSLM/rIUiCqwbi3DeLXNvlVAiDv3/cTBxwXeKUtIVDiyGmK0ecV0tyjRws57fKOKhazhmQUr2WJ1/N5gmhxrnEB8KysE/iiuvchi4aaaMCN2nZ9bFjxkCgYBVg6yH3cHD42kJZkbk8CaGpgeK+OwErAnuO9Oxv2uoAH0shVcAePz9oIAMl3peIFNB8VATs0+cnUyXYVIr8sqLdM7Ru86fb232POAmYRRwxss8oqvDU+7iyY3ygeTtLU+A9UmUnUvhQMC+eOkcZSGQUS1nM6ATtz93Jl5EfDthbA==";
//        System.out.println(pubKey);
//        System.out.println(Crypto.Encrypt("PINCODETEST123456", pubKey, false));
        System.out.println(Crypto.Decrypt("XgUHrIC5rtTas4PjK4osCFgFTSCvLBURMGfX4MdZ/HrBczJ5UT1P9hdMHPdDheFC3I/KZYdtHafJd942Hra7TlKX/dQKCl2zx+lSc3EpZhXIx9taKrfLHuC51kdwOf5O7b9Re9GOTtAVI2hyfMxlQqWHP275rH6Y/8YaiEKEl+ZOYa9q2Cy3UcNHtcRIdFLGW2bG13L6O5Ae72teoIWVEQh4T0QKXA7k/sTjdrJoBVednQZ9Jp6qDxN47vcWja1YLdI+dxEQBGYWjAKu+dG7+cyR2/D42VWNJ/SuipENQghbkOO3ZExykMFj/AwTDhBVP5ZFI0lWnlDP+A176jWUqQ==", pubKey, false));
    }
}
