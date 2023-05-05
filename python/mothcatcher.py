import scrapy
from urllib.parse import urljoin
import urllib.request
from ..items import MothItem
import ssl
# do not look at the fact that did not use ssl
# bad security
ssl._create_default_https_context = ssl._create_unverified_context

class MothcatcherSpider(scrapy.Spider):
    name = 'mothcatcher'
    # not the only domain i caught, but this is one domain the images were receivved from
    allowed_domains = ['www.mothsireland.com']
    start_urls=['https://www.mothsireland.com/gallery3/index.php/tag_albums/album/10/Common-Moths',
    'https://www.mothsireland.com/gallery3/index.php/tag_albums/album/11/Day-Flying-Moths']
    prefix='https://www.mothsireland.com'

    custom_settings = {
        "ITEM_PIPELINES": {'scrapy.pipelines.images.ImagesPipeline': 1},
        "IMAGES_STORE": "/home/nmq-hyt/Projects/python/moth/images"
    }
    # "parse" the html
    def parse(self, response):
        item = MothItem()
        print(self.start_urls)
        for img in response.css ('img'):
            img_url = urljoin(self.prefix,img.xpath('@src').get())
            print(img_url)
            item['image_urls'] = img_url
            hashurl=str(hash(img_url))
            fiel = open(r"/home/nmq-hyt/Desktop/images/"+hashurl+".jpg","a+")
            urllib.request.urlretrieve(img_url,"/home/nmq-hyt/Desktop/images/"+hashurl+".jpg")
            fiel.close()
        return None