package cn.chinogo.search.service.impl;

import cn.chinogo.constant.Const;
import cn.chinogo.mapper.TbItemMapper;
import cn.chinogo.pojo.*;
import cn.chinogo.search.service.SearchService;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Solr Service 实现类
 *
 * @author chinotan
 */
@Service(version = Const.CHINOGO_SEARCH_VERSION, timeout = 1000000)
public class SearchServiceImpl implements SearchService {

    private static Logger logger = Logger.getLogger(SearchServiceImpl.class);

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private TbItemMapper itemMapper;

    @Value("${elastic.item.index}")
    private String ITEMINDEX;

    @Value("${elastic.item.type}")
    private String ITEMTYPE;

    @Override
    public ChinogoResult importAllItems() {
        BulkRequestBuilder bulk = transportClient.prepareBulk();

        XContentBuilder builder = null;

        List<SearchItem> searchItemsList = itemMapper.getItemListWithDescAndCid();

        for (SearchItem searchItem : searchItemsList) {
            try {
                builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("image", searchItem.getImage())
                        .field("price", searchItem.getPrice())
                        .field("sell_point", searchItem.getSellPoint())
                        .field("title", searchItem.getTitle())
                        .field("colour", searchItem.getColour())
                        .field("size", searchItem.getSize())
                        .field("weight", searchItem.getWeight().toString())
                        .field("category_name", searchItem.getCategoryName())
                        .field("item_desc", searchItem.getItemDesc())
                        .endObject();

                bulk.add(transportClient.prepareIndex(ITEMINDEX, ITEMTYPE, searchItem.getId()).setSource(builder));
            } catch (Exception e) {
                return ChinogoResult.build(500, e.getMessage());
            }
        }

        bulk.get();

        return ChinogoResult.ok();
    }

    @Override
    public SearchResult search(String queryString, Integer page, Integer rows, Integer sort, Long priceGt, Long priceLte) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().
                should(QueryBuilders.multiMatchQuery(queryString, "title", "sell_point", "item_desc", "category_name"));

        // 最大值，最小值
        if (priceGt != 0 && priceLte != 0) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(priceGt).lte(priceLte));
        }

        // 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("sell_point");
        highlightBuilder.preTags("<span style='color: red;'>");
        highlightBuilder.postTags("</span>");

        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(ITEMINDEX)
                .setTypes(ITEMTYPE)
                .setQuery(boolQueryBuilder)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setFrom(page * rows)
                .setSize(rows)
                .highlighter(highlightBuilder);

        // 排序
        if (sort == 1) {
            // 正序
            requestBuilder.addSort("price", SortOrder.ASC);
        } else if (sort == -1) {
            // 倒序
            requestBuilder.addSort("price", SortOrder.DESC);
        }

        SearchHits searchHits = requestBuilder
                .get()
                .getHits();

        List<SearchItem> list = new ArrayList();
        for (SearchHit hit : searchHits) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            // 判断是否存在高亮域
            String sellPoint = "";
            String title = "";

            Map<String, Object> source = hit.getSource();

            if (highlightFields != null && !highlightFields.isEmpty()) {
                Set<Map.Entry<String, HighlightField>> entries = highlightFields.entrySet();
                for (Map.Entry<String, HighlightField> entry : entries) {
                    source.put(entry.getKey().toString(), entry.getValue().fragments()[0].toString());
                }
            }

            String id = hit.getId();
            String image = (String) source.get("image");
            BigDecimal price = new BigDecimal(source.get("price").toString());
            sellPoint = (String) source.get("sell_point");
            title = (String) source.get("title");
            String colour = (String) source.get("colour");
            String size = (String) source.get("size");
            BigDecimal weight = new BigDecimal(String.valueOf(source.get("weight")));

            SearchItem searchItem = new SearchItem(id, image, price, sellPoint, title, colour, weight, size);

            list.add(searchItem);
        }

        SearchResult searchResult = new SearchResult();

        searchResult.setItemList(list);
        searchResult.setCurPage(page + 1);
        searchResult.setPageCount(rows);
        searchResult.setPerNum(list.size());
        searchResult.setRecordCount(searchHits.totalHits);

        return searchResult;
    }

    @Override
    public ChinogoResult updateItem(SearchItem item) throws Exception {
        UpdateRequest request = new UpdateRequest(ITEMINDEX, ITEMTYPE, item.getId());

        XContentBuilder xContentBuilder = null;
        try {
            xContentBuilder = XContentFactory.jsonBuilder().startObject();
            if (!StringUtils.isEmpty(item.getCategoryName())) {
                xContentBuilder.field("category_name", item.getCategoryName());
            }
            if (!StringUtils.isEmpty(item.getImage())) {
                xContentBuilder.field("image", item.getImage());
            }
            if (!StringUtils.isEmpty(item.getItemDesc())) {
                xContentBuilder.field("item_desc", item.getItemDesc());
            }
            if (!StringUtils.isEmpty(item.getSellPoint())) {
                xContentBuilder.field("sell_point", item.getSellPoint());
            }
            if (!StringUtils.isEmpty(item.getTitle())) {
                xContentBuilder.field("title", item.getTitle());
            }
            if (item.getPrice() != null && (item.getPrice().compareTo(BigDecimal.ZERO)) > 0) {
                xContentBuilder.field("price", item.getPrice());
            }
            if (item.getWeight() != null && (item.getWeight().compareTo(BigDecimal.ZERO)) > 0) {
                xContentBuilder.field("weight", item.getWeight());
            }
            if (StringUtils.isNotBlank(item.getColour())) {
                xContentBuilder.field("colour", item.getColour());
            }
            if (StringUtils.isNotBlank(item.getSize())) {
                xContentBuilder.field("size", item.getSize());
            }
            xContentBuilder.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        request.doc(xContentBuilder);
        transportClient.update(request).get();

        return ChinogoResult.ok();
    }

    @Override
    public ChinogoResult deleteItem(String id) throws Exception {
        try {
            transportClient.prepareDelete(ITEMINDEX, ITEMTYPE, id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ChinogoResult.ok();
    }

    @Override
    public ChinogoResult addItem(Long id) throws Exception {
        BulkRequestBuilder bulk = transportClient.prepareBulk();

        XContentBuilder builder = null;

        SearchItem item = itemMapper.getItemListWithDescAndCidById(id);

        try {
            builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("image", item.getImage())
                    .field("price", item.getPrice())
                    .field("sell_point", item.getSellPoint())
                    .field("title", item.getTitle())
                    .field("colour", item.getColour())
                    .field("size", item.getSize())
                    .field("weight", item.getWeight().toString())
                    .field("category_name", item.getCategoryName())
                    .field("item_desc", item.getItemDesc())
                    .endObject();

            bulk.add(transportClient.prepareIndex(ITEMINDEX, ITEMTYPE, item.getId()).setSource(builder));
        } catch (Exception e) {
            return ChinogoResult.build(500, e.getMessage());
        }

        bulk.get();

        return ChinogoResult.ok();
    }
}
