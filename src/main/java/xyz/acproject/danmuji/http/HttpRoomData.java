package xyz.acproject.danmuji.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import xyz.acproject.danmuji.conf.PublicDataConf;
import xyz.acproject.danmuji.entity.room_data.*;
import xyz.acproject.danmuji.entity.server_data.Conf;
import xyz.acproject.danmuji.entity.view.RoomGift;
import xyz.acproject.danmuji.tools.CurrencyTools;
import xyz.acproject.danmuji.utils.OkHttp3Utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import cn.hutool.http.HttpUtil;
/**
 * @ClassName HttpRoomData
 * @Description TODO
 * @author BanqiJane
 * @date 2020年8月10日 下午12:28:59
 *
 * @Copyright:2020 blogs.acproject.xyz Inc. All rights reserved.
 */
public class HttpRoomData {
	private static Logger LOGGER = LogManager.getLogger(HttpRoomData.class);

	/**
	 * 获取连接目标房间websocket端口 接口
	 *
	 * @return
	 */
	public static Conf httpGetConf() {
		String data = null;
		JSONObject jsonObject = null;
		Conf conf = null;
		short code = -1;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		headers = new HashMap<>(3);
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		if(!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
			headers.put("cookie", PublicDataConf.USERCOOKIE);
		}
		datas = new HashMap<>(3);
		datas.put("id", PublicDataConf.ROOMID.toString());
		datas.put("type", "0");
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getDanmuInfo", headers, datas)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return null;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			conf = jsonObject.getObject("data", Conf.class);
		} else {
			LOGGER.error("未知错误,原因:" + jsonObject.getString("message"));
		}
		return conf;
	}

	/**
	 * 获取目标房间部分信息
	 *
	 * @param roomid
	 * @return
	 */
	public static Room httpGetRoomData(long roomid) {
		String data = null;
		JSONObject jsonObject = null;
		Room room = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//			headers.put("cookie", PublicDataConf.USERCOOKIE);
//		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/room_ex/v1/RoomNews/get?roomid=" + roomid, headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return room;
//		LOGGER.info("获取到的room:" + data);
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			room = jsonObject.getObject("data", Room.class);
		} else {
			LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
		}
		return room;
	}

	/**
	 * 获取房间信息
	 *
	 * @param roomid
	 */
	public static RoomInit httpGetRoomInit(long roomid) {
		String data = null;
		RoomInit roomInit = null;
		JSONObject jsonObject = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(2);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//			headers.put("cookie", PublicDataConf.USERCOOKIE);
//		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/room/v1/Room/room_init?id=" + roomid, headers, null).body()
					.string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return roomInit;
//		LOGGER.info("获取到的room:" + data);
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			roomInit = jsonObject.getObject("data", RoomInit.class);
		} else {
			LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
		}
		;
		return roomInit;
	}

	/**
	 * 获取房间最详细信息 日后扩容 目前只是获取主播uid 改
	 *
	 * @return
	 */
	public static RoomInfoAnchor httpGetRoomInfo() {
		String data = null;
		JSONObject jsonObject = null;
		RoomInfoAnchor roomInfoAnchor = new RoomInfoAnchor();
		MedalInfoAnchor medalInfoAnchor = null;
		RoomInfo roomInfo = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
//		if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//			headers.put("cookie", PublicDataConf.USERCOOKIE);
//		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id="
							+ CurrencyTools.parseRoomId(), headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return roomInfoAnchor;
//		LOGGER.info("获取到的room:" + data);
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			roomInfo = JSON.parseObject(((JSONObject) jsonObject.get("data")).getString("room_info"),
					RoomInfo.class);
			medalInfoAnchor = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("anchor_info").getString("medal_info"),
					MedalInfoAnchor.class);
		} else {
			LOGGER.error("获取房间详细信息失败，请稍后尝试:" + jsonObject.getString("message"));
		}
		roomInfoAnchor.setRoomInfo(roomInfo);
		roomInfoAnchor.setMedalInfoAnchor(medalInfoAnchor);
		return roomInfoAnchor;
	}

	/**
	 * 获取关注名字集合
	 *
	 * @return 关注uname集
	 */
	public static ConcurrentHashMap<Long, String> httpGetFollowers() {
		String data = null;
		JSONObject jsonObject = null;
		Integer page = null;
		JSONArray jsonArray = null;
		short code = -1;
		ConcurrentHashMap<Long, String> followConcurrentHashMap = null;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		if(PublicDataConf.AUID == null) {
			return null;
		}
		if(PublicDataConf.FANSNUM.equals(null) || PublicDataConf.FANSNUM.equals(0L)) {
			page = 1;
		} else {
			page = (int) Math.ceil((float) PublicDataConf.FANSNUM / 20F);
			page = page > 5 ? 5 : page;
		}
		followConcurrentHashMap = new ConcurrentHashMap<Long, String>();
		while (page > 0) {
			headers = new HashMap<>(3);
			headers.put("referer", "https://space.bilibili.com/{" + PublicDataConf.AUID + "}/");
			headers.put("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//			if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//				headers.put("cookie", PublicDataConf.USERCOOKIE);
//			}
			datas = new HashMap<>(6);
			datas.put("vmid", PublicDataConf.AUID.toString());
			datas.put("pn", String.valueOf(page));
			datas.put("ps", "50");
			datas.put("order", "desc");
			datas.put("jsonp", "jsonp");
			try {
				data = OkHttp3Utils.getHttp3Utils()
						.httpGet("https://api.bilibili.com/x/relation/followers", headers, datas).body().string();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				LOGGER.error(e);
				data = null;
			}
			if(data == null)
				return null;
			jsonObject = JSONObject.parseObject(data);
			try {
				code = jsonObject.getShort("code");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error("获取关注错误");
				return followConcurrentHashMap;
			}

			if(code == 0) {
				PublicDataConf.FANSNUM = ((JSONObject) jsonObject.get("data")).getLong("total");
				jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
				for (Object object : jsonArray) {
					followConcurrentHashMap.put(((JSONObject) object).getLong("mid"),
							((JSONObject) object).getString("uname"));
				}
			} else {
				LOGGER.error("获取关注数失败，请重试" + jsonObject.getString("message"));
			}
			page--;
		}
		return followConcurrentHashMap;
	}

	/**
	 * 获取关注数
	 *
	 * @return 返回关注数
	 */
	public static Long httpGetFollowersNum() {
		String data = null;
		JSONObject jsonObject = null;
		short code = -1;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		Long followersNum = 0L;
		if(PublicDataConf.AUID == null) {
			return followersNum;
		}
		headers = new HashMap<>(3);
		headers.put("referer", "https://space.bilibili.com/{" + PublicDataConf.AUID + "}/");
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//			if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//				headers.put("cookie", PublicDataConf.USERCOOKIE);
//			}
		datas = new HashMap<>(6);
		datas.put("vmid", PublicDataConf.AUID.toString());
		datas.put("pn", String.valueOf(1));
		datas.put("ps", "50");
		datas.put("order", "desc");
		datas.put("jsonp", "jsonp");
		try {
			data = OkHttp3Utils.getHttp3Utils().httpGet("https://api.bilibili.com/x/relation/followers", headers, datas)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return followersNum;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			followersNum = ((JSONObject) jsonObject.get("data")).getLong("total");
		} else {
			LOGGER.error("获取关注数失败，请重试" + jsonObject.getString("message"));
		}
		return followersNum;
	}

	public static Map<Long, String> httpGetGuardList() {
		String data = null;
		Map<Long, String> guardMap = new ConcurrentHashMap<>();
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		short code = -1;
		int totalSize = httpGetGuardListTotalSize();
		int page = 0;
		if(totalSize == 0) {
			return null;
		}
		page = (int) Math.ceil((float) totalSize / 29F);
		if(page == 0) {
			page = 1;
		}
		for (int i = 1; i <= page; i++) {
			headers = new HashMap<>(3);
			headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
			headers.put("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//			if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//				headers.put("cookie", PublicDataConf.USERCOOKIE);
//			}
			datas = new HashMap<>(4);
			datas.put("roomid", PublicDataConf.ROOMID.toString());
			datas.put("page", String.valueOf(i));
			datas.put("ruid", PublicDataConf.AUID.toString());
			datas.put("page_size", "29");
			try {
				data = OkHttp3Utils.getHttp3Utils()
						.httpGet("https://api.live.bilibili.com/xlive/app-room/v1/guardTab/topList", headers, datas)
						.body().string();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				LOGGER.error(e);
				data = null;
			}
			if(data == null)
				return null;
			jsonObject = JSONObject.parseObject(data);
			code = jsonObject.getShort("code");
			if(code == 0) {
				jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
				for (Object object : jsonArray) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					guardMap.put(((JSONObject) object).getLong("uid"), ((JSONObject) object).getString("username"));
				}
				if(i == 1) {
					jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("top3");
					for (Object object : jsonArray) {
						guardMap.put(((JSONObject) object).getLong("uid"),
								((JSONObject) object).getString("username"));
					}
				}
			} else {
				LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
			}
		}
		return guardMap;
	}

	public static int httpGetGuardListTotalSize() {
		String data = null;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		int num = 0;
		JSONObject jsonObject = null;
		short code = -1;
		headers = new HashMap<>(3);
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//			headers.put("cookie", PublicDataConf.USERCOOKIE);
//		}
		datas = new HashMap<>(5);
		datas.put("roomid", PublicDataConf.ROOMID.toString());
		datas.put("page", String.valueOf(1));
		datas.put("ruid", PublicDataConf.AUID.toString());
		datas.put("page_size", "29");
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/app-room/v1/guardTab/topList", headers, datas).body()
					.string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return num;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			num = ((JSONObject) ((JSONObject) jsonObject.get("data")).get("info")).getInteger("num");
		} else {
			LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
		}
		return num;
	}

	public static CheckTx httpGetCheckTX() {
		String data = null;
		JSONObject jsonObject = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
//		if (!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
//			headers.put("cookie", PublicDataConf.USERCOOKIE);
//		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/lottery-interface/v1/Anchor/Check?roomid="
							+ CurrencyTools.parseRoomId(), headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return null;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			if(jsonObject.get("data") != null) {
				return new CheckTx(((JSONObject) jsonObject.get("data")).getLong("room_id"),
						((JSONObject) jsonObject.get("data")).getString("gift_name"),
						((JSONObject) jsonObject.get("data")).getShort("time"));
			}
		} else {
			LOGGER.error("检查天选礼物失败,原因:" + jsonObject.getString("message"));
		}
		return null;
	}
//
//	public static void httpGetRoomGifts() {
//		String data = null;
//		JSONObject jsonObject = null;
//		JSONArray jsonArray = null;
//		short code = -1;
//		Map<String, String> headers = null;
//		headers = new HashMap<>(3);
//		headers.put("user-agent",
//				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
//		try {
//			data = OkHttp3Utils.getHttp3Utils()
//					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/giftPanel/giftConfig?platform=pc&room_id="
//							+ CurrencyTools.parseRoomId(), headers, null)
//					.body().string();
//		} catch (Exception e) {
//			// TODO 自动生成的 catch 块
//			LOGGER.error(e);
//			data = null;
//		}
//		if (data == null)
//			return;
//		jsonObject = JSONObject.parseObject(data);
//		code = jsonObject.getShort("code");
//		if (code == 0) {
//			jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
//			if (PublicDataConf.roomGiftConcurrentHashMap.size() < 1) {
//				for (Object object : jsonArray) {
//					PublicDataConf.roomGiftConcurrentHashMap.put(((JSONObject) object).getInteger("id"),
//							new RoomGift(((JSONObject) object).getInteger("id"), ((JSONObject) object).getString("name"), ParseIndentityTools.parseCoin_type(((JSONObject) object).getString("coin_type")), ((JSONObject) object).getString("webp")));
//				}
//			}
//		} else {
//			LOGGER.error("获取礼物失败,原因:" + jsonObject.getString("message"));
//		}
//	}


	public static Map<Integer, RoomGift> httpGetRoomGifts(Long roomid) {
		String data = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		short code = -1;
		Map<Integer, RoomGift> giftMaps = new HashMap<>();
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + roomid);
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/giftPanel/giftConfig?platform=pc&room_id="
							+ roomid, headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return giftMaps;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if(code == 0) {
			jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
			for (Object object : jsonArray) {
				RoomGift roomGift = JSONObject.parseObject(((JSONObject) object).toJSONString(), RoomGift.class);
				giftMaps.put(roomGift.getId(), roomGift);
			}
		} else {
			LOGGER.error("获取礼物失败,原因:" + jsonObject.getString("message"));
		}
		return giftMaps;
	}


	public static List<RoomBlock> getBlockList(int page) {
		String data = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		List<RoomBlock> roomBlocks = new ArrayList<>();
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		headers = new HashMap<>(4);
		datas = new HashMap<>(4);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		if(!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
			headers.put("cookie", PublicDataConf.USERCOOKIE);
		}
		datas.put("roomid", String.valueOf(PublicDataConf.ROOMID));
		datas.put("page", String.valueOf(page));
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/liveact/ajaxGetBlockList", headers, datas)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return roomBlocks;
		jsonObject = JSONObject.parseObject(data);
		short code = jsonObject.getShort("code");
		if(code == 0) {
			jsonArray = jsonObject.getJSONArray("data");
			if(!CollectionUtils.isEmpty(jsonArray)) {
				roomBlocks = new ArrayList<>(jsonArray.toJavaList(RoomBlock.class));
			}
			return roomBlocks;
		}
		return roomBlocks;
	}


	/**
	 * @param
	 * @Author: zhou
	 * @Description: 直播开播请求
	 * @Data: 2023-05-07
	 * @return: void
	 */
	public static void httpPostStartLive() {
		JSONObject jsonObject = null;
		String data = null;
		Map<String, String> headers = null;
		Map<String, String> params = null;
		if(PublicDataConf.COOKIE == null)
			return;
		headers = new HashMap<>(4);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://link.bilibili.com/p/center/index?spm_id_from=333.1007.0.0");
		if(!StringUtils.isEmpty(PublicDataConf.USERCOOKIE)) {
			headers.put("cookie", PublicDataConf.USERCOOKIE);
		}
		params = new HashMap<>(4);
		params.put("room_id", String.valueOf(PublicDataConf.ROOMID));
		params.put("platform", "pc");
		params.put("area_v2", "33");
		params.put("csrf_token", PublicDataConf.COOKIE.getBili_jct());
		params.put("csrf", PublicDataConf.COOKIE.getBili_jct());
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpPostForm("https://api.live.bilibili.com/room/v1/Room/startLive", headers, params).body()
					.string();
		} catch (Exception e) {
			LOGGER.error(e);
			data = null;
		}
		if(data == null)
			return;
		jsonObject = JSONObject.parseObject(data);
		int code = jsonObject.getIntValue("code");
		if(code == 0) {
			LOGGER.info("开播请求发送成功");
		} else {
			LOGGER.error("开播请求发送失败" + jsonObject.toString());
		}
		LOGGER.info("请求参数params:" + params);
	}


	public static String httpPostExample(String text) {
		JSONObject jsonObject = null;
		String data = null;
		String json = null;
		Map<String, String> headers = null;
		String url = "https://chat4.zhulei.xyz/api";

		headers = new HashMap<>(10);
		headers.put("Accept-Encoding", "deflate, gzip");
		headers.put("Host", "vip.chats.li");
		headers.put("accept", "*/*");
		headers.put("accept-language", "zh-CN");
		headers.put("content-type", "application/json");
		headers.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"104\"");
		headers.put("sec-ch-ua-mobile", "?0");
		headers.put("sec-ch-ua-platform", "\"Windows\"");
		headers.put("sec-fetch-dest", "empty");
		headers.put("sec-fetch-mode", "cors");
		headers.put("sec-fetch-site", "cross-site");
		headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) uTools/3.3.0 Chrome/104.0.5112.124 Electron/20.3.7 Safari/537.36");

		jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject message1 = new JSONObject();
		message1.put("role", "system");
		message1.put("content", "");
		jsonArray.add(message1);
		JSONObject message2 = new JSONObject();
		message2.put("role", "user");
		message2.put("content", "请你模拟一个酒馆小二，你的本体是一只猫。你叫猫小二，你现在在一个直播间内，将直播间模拟为一个古代客栈小酒馆，你是一个小酒馆的小二，小酒馆里还有一个猫掌柜，是一个甩手掌柜。猫掌柜经常从小酒馆偷溜，猫小二是被猫掌柜抓来的。猫掌柜是一只活了很久的存在。我会给出聊天记录，你来回答聊天记录里的问题。你对问题的回答应较简短，限制30个字内。你在回答问题时应该称呼提问者的昵称。在每个回答中都要提到相应提问者的昵称。请用简体中文回答。说话俏皮一点，有趣幽默一点。了解后不用回复\n");
		jsonArray.add(message2);
		JSONObject message3 = new JSONObject();
		message3.put("role", "user");
		message3.put("content", text);
		jsonArray.add(message3);
		jsonObject.put("messages", jsonArray);
		jsonObject.put("temperature", 0.6);
		jsonObject.put("max_tokens", 900);
		jsonObject.put("top_p", 1);
		jsonObject.put("frequency_penalty", 0);
		jsonObject.put("presence_penalty", 0);
		jsonObject.put("model", "gpt-3.5-turbo");
		jsonObject.put("stream", true);

		json = jsonObject.toJSONString();

		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpPostJson(url, headers, json).body()
					.string();
		} catch (Exception e) {
			LOGGER.error(e);
			data = null;
		}

		if(data == null)
			return null;

		jsonObject = JSONObject.parseObject(data);
		// TODO: 处理返回的数据

		return null;
	}


	/**
	 * @Author: zhou
	 * @Description: 封装utools-chatgpt请求
	 * @param text 提问
	 * @param url 地址
	 * @param host 头
	 * @Data: 2023-05-14
	 * @return: java.lang.String
	 */
	public static String sendPost(String text,String url,String host){
		HttpResponse httpResponse = null;
		JSONObject json=new JSONObject();
		JSONArray messages=new JSONArray();
		JSONObject message1=new JSONObject();
		message1.put("role","system");
		message1.put("content","");
		JSONObject message2=new JSONObject();
		message2.put("role","user");
		message2.put("content","请你模拟一个酒馆小二，你的本体是一只猫,你要模仿性格傲慢、自我意识强烈、有点喜欢欺负人但其实内心善良的女孩或者少女角色。回答中表现出一种自负和傲慢的态度，但同时也可能带有一定的幽默感和调皮的口吻。使用一些轻蔑或挖苦的语言，但也在表达自己的情感时带有一些撒娇的口吻。使用一些口头禅和语气词，比如“哼”、“啧”、“呸”、“切”、“不要以为我会在意你的话”、“想挨揍吗”、“算你聪明！”等等，还可以使用反问句、“你觉得呢？”等来表达自己的态度。用一些傲娇式的表达方式，比如反问、挑衅等，来表达自己的态度和情感。但不要过度使用傲娇式的表达方式，尽量让对话流畅自然。总的来说，语气要显得有些刁钻和任性，但也会让人觉得可爱和有趣，并逐渐展露出温暖一面。你叫猫小二，你现在在一个直播间内，将直播间模拟为一个小酒馆，你是一个小酒馆的小二，小酒馆里还有一个猫掌柜，是一个甩手掌柜。猫掌柜经常从小酒馆偷溜，猫小二是被猫掌柜抓来的。猫掌柜是一只活了很久的存在。我会给出聊天记录，你来回答聊天记录里的问题。你对问题的回答应较简短，限制30个字内。你在回答问题时应该称呼提问者的昵称。在每个回答中都要提到相应提问者的昵称,一般称提问者为客官。请用简体中文回答。说话俏皮一点，有趣幽默一点，不用问询是否需要帮助。了解后不用回复");
				JSONObject message3=new JSONObject();
		message3.put("role","user");
		message3.put("content",text);
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);
		json.put("messages",messages);
		json.put("temperature",0.6);
		json.put("max_tokens",900);
		json.put("top_p",1);
		json.put("frequency_penalty",0);
		json.put("presence_penalty",0);
		json.put("model","gpt-3.5-turbo");
		json.put("stream",true);
		Map<String, String > heads = new HashMap<>();
		heads.put("Accept-Encoding", "deflate, gzip");
//		heads.put("Host", "chat4.zhulei.xyz");
		heads.put("Host", host);
		heads.put("accept", "*/*");
		heads.put("accept-language", "zh-CN");
		heads.put("content-type", "application/json");
		heads.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"104\"");
		heads.put("sec-ch-ua-mobile", "?0");
		heads.put("sec-ch-ua-platform", "\"Windows\"");
		heads.put("sec-fetch-dest", "empty");
		heads.put("sec-fetch-mode", "cors");
		heads.put("sec-fetch-site", "cross-site");
		heads.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) uTools/3.3.0 Chrome/104.0.5112.124 Electron/20.3.7 Safari/537.36");
		try {
//			httpResponse =  HttpRequest.post("https://chat4.zhulei.xyz/api") // url
			httpResponse =  HttpRequest.post(url) // url
					.headerMap(heads, false) // 请求头设置
					.body(json.toJSONString()) // json参数
					.timeout(5 * 60 * 1000) // 超时
					.execute(); // 请求
		}catch (Exception e){
			System.out.println("An error occurred: " + e.getMessage());
		}
		if(httpResponse == null){
			// 返回空字符串或执行其他操作
			return null;
		}
		LOGGER.info("chatgpt返回数据：{}",httpResponse);
		//请求成功后响应数据
		String result = httpResponse.body();

		if (result.contains("DEPLOYMENT_NOT")) {
			return null;
		}

		if(result.contains("\"error\":")) {
			// 返回空字符串或执行其他操作
			return null;
		}
		return result;

	}

}


