package com.mytijian.admin.web.util;

import com.google.common.collect.Lists;
import com.mytijian.card.enums.CardTypeEnum;
import com.mytijian.card.model.Card;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by shjh on 2017/11/14.
 */
public class ManageCardUtil {
        public static final Logger logger = LoggerFactory.getLogger(ManageCardUtil.class);

        public ManageCardUtil() {
        }

        public static boolean isParentCard(Card card) {
            return card.getType() == CardTypeEnum.VIRTUAL.getCode() && card.getParentCardId() == null;
        }

        public static List<List<String>> getExportCardNum(List<Map<String, String>> cardMaps) throws IOException, InvalidFormatException {
            List<List<String>> cardList = new ArrayList<>();
            ArrayList needKeys = Lists.newArrayList();
            needKeys.add("cardnum");
            needKeys.add("cardpwd");
            Iterator var6 = cardMaps.iterator();

            while(var6.hasNext()) {
                Map cardmap = (Map)var6.next();
                List<String> eachCard = new ArrayList();

                for(int index = 0; index < needKeys.size(); ++index) {
                    eachCard = getExportCheckCardValue(cardmap, needKeys);
                }

                cardList.add(eachCard);
            }
            return cardList;
        }

        private static List<String> getExportCheckCardValue(Map cardmap, List<String> needKeys) {
            ArrayList eachCard = Lists.newArrayList();
            Iterator var3 = needKeys.iterator();

            while(var3.hasNext()) {
                String key = (String)var3.next();

                try {
                    if(cardmap.containsKey(key)) {
                        eachCard.add(String.valueOf(cardmap.get(key)));
                    }
                } catch (Exception var6) {
                    logger.error("getExportCheckOrderValue error : key is {} and orderMap is {}", new Object[]{var6, key, cardmap});
                }
            }

            return eachCard;
        }

        public static String getCardNumTemplate(String templatePath) {
            return templatePath + File.separator + "cardsegment.xls";
        }
}
