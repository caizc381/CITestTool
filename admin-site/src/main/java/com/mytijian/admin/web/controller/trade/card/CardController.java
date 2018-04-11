package com.mytijian.admin.web.controller.trade.card;

import com.mytijian.account.exceptions.LoginFailedException;
import com.mytijian.admin.web.controller.trade.card.util.ExcelUtil;
import com.mytijian.admin.web.util.ManageCardUtil;
import com.mytijian.uic.annotation.LoginRequired;
import com.mytijian.card.dto.crm.CardManageDto;
import com.mytijian.card.dto.crm.CardRecordDto;
import com.mytijian.card.enums.CardStatusEnum;
import com.mytijian.card.enums.CardTypeEnum;
import com.mytijian.card.model.Card;
import com.mytijian.card.service.CardManageService;
import com.mytijian.card.service.CardSequenceService;
import com.mytijian.card.service.CardService;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.pulgin.mybatis.pagination.PageView;
import com.mytijian.util.AssertUtil;
import com.mytijian.util.DateUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class CardController {

	public static final Logger logger = LoggerFactory.getLogger(CardController.class);

	@Resource(name = "cardService")
	private CardService cardService;

	@Resource(name = "cardManageService")
	private CardManageService cardManageService;

	@Resource(name ="cardSequenceService")
	private CardSequenceService cardSequenceService;

	@Value("${temp.folder}")
	private String tempFolder;

	@RequestMapping(value = "/card", method = RequestMethod.GET)
	@ResponseBody
	@LoginRequired
	public Map<String, Object> getCardList(@RequestParam("accountId") Integer accountId, Page page, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		PageView<CardManageDto> cardList = cardManageService.getCardManageListByPage(accountId, page);
		map.put("cardList", cardList);
		List<CardManageDto> totalList = cardManageService.getCardManageList(accountId);
		if (AssertUtil.isNotEmpty(totalList)) {
			map.put("totalCard", totalList.size());
			Optional<Integer> cardTotalMoney = totalList.stream()
					.filter(card -> card.getStatus() == CardStatusEnum.USABLE.getCode())
					.filter(card ->{
						Long timeNum = new Date().getTime();
						if(timeNum <= card.getExpiredDate().getTime()){
							return true;
						} else {
							return false;
						}
					})
					.map(card -> card.getBalance().intValue())
					.reduce((sum, balance) -> sum + balance);
			if (cardTotalMoney.isPresent()) {
				map.put("cardTotalMoney", cardTotalMoney.get());
			}
		}
		return map;
	}


	/**
	 * 生成号段
	 * @param increment
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/generCardNum", method = RequestMethod.GET)
	@ResponseBody
	public void generCardNum(@RequestParam("increment") Integer increment, HttpSession session, HttpServletResponse response) throws IOException, InvalidFormatException {
		Long cardSeq = cardSequenceService.getSequenceByName("card_num_seq", increment);
		//生成
		List<Map<String,String>> cardMaps = cardService.generCardNum(cardSeq,Long.valueOf(increment));
		InputStream tplInputStream = getClass().getClassLoader().getResourceAsStream(
				"ordertemplate/cardsegment.xls");
		//导出

			List<List<String>> exportCardNum = ManageCardUtil.getExportCardNum(cardMaps);
			HSSFWorkbook workbook = ExcelUtil.createHSSFWorkbookByTemplate(exportCardNum,tplInputStream);

			// 输出excel到客户端
			String origin = response.getHeader("Access-Control-Allow-Origin");
			response.reset();
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + "cardsegment_" + DateUtils.format(DateUtils.YYYYMMDDSS, new Date())
							+ ".xls");
			response.addHeader("Access-Control-Allow-Credentials", "true");
			response.addHeader("Access-Control-Allow-Origin", origin);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			OutputStream outputStream = response.getOutputStream();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			IOUtils.copy(bais, outputStream);
			outputStream.flush();
			outputStream.close();
			tplInputStream.close();
			baos.close();
			bais.close();

	}



	@RequestMapping(value = "/revokeCard", method = { RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	public void revocCard(@RequestBody CardRecordDto cardRecord) throws LoginFailedException {
		boolean cleanCardRecord = false;
		Card card = cardRecord.getCard();
		Integer managerId = card.getManagerId();
		List<CardRecordDto> cardRecords = new ArrayList<>();
		cardRecords.add(cardRecord);
		if (CardTypeEnum.ENTITY.getCode().equals(card.getType())){
			this.cardService.revokeCards(Collections.singletonList(card.getId()),managerId,cleanCardRecord);
		}else {
			this.cardService.revokeCard(cardRecords, managerId, cleanCardRecord);
		}
	}

	@RequestMapping(value = "/recoverBalance", method = { RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	public void recoverBalance(@RequestBody CardRecordDto cardRecord) throws LoginFailedException {
		Integer managerId = cardRecord.getCard().getManagerId();
		Card card = cardRecord.getCard();
		List<CardRecordDto> cardRecords = new ArrayList<>();
		cardRecords.add(cardRecord);
		if (CardTypeEnum.ENTITY.getCode().equals(card.getType())){
			this.cardService.recoverCardBalances(Collections.singletonList(card.getId()),managerId);
		}else {
			this.cardService.recoverBalance(cardRecords, managerId);

		}
	}
}
